import { useEffect, useState } from "react";
import { axiosClient } from "../../api/axiosClient";
import { useNotifications } from "../../context/useNotifications";

const returnTimeOptions = [
  { label: "4 hours", hours: 4 },
  { label: "24 hours", hours: 24 },
  { label: "48 hours", hours: 48 },
  { label: "72 hours", hours: 72 },
];

function getLocalDateTimeValue(date) {
  const timezoneOffset = date.getTimezoneOffset() * 60000;
  return new Date(date.getTime() - timezoneOffset).toISOString().slice(0, 16);
}

function CheckoutModal({
  isOpen,
  checkoutMode,
  initialAsset = null,
  initialBorrower = null,
  onClose,
  onCheckoutSuccess,
}) {
  const { notifySuccess, notifyError } = useNotifications();
  const [assetSearch, setAssetSearch] = useState("");
  const [borrowerSearch, setBorrowerSearch] = useState("");
  const [assetResults, setAssetResults] = useState([]);
  const [borrowerResults, setBorrowerResults] = useState([]);

  const [selectedAsset, setSelectedAsset] = useState(initialAsset);
  const [selectedBorrower, setSelectedBorrower] = useState(initialBorrower);
  const [returnAt, setReturnAt] = useState("");
  const [selectedReturnHours, setSelectedReturnHours] = useState(null);

  const [isSearching, setIsSearching] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [checkoutError, setCheckoutError] = useState("");

  useEffect(() => {
    if (isOpen) {
      setSelectedAsset(initialAsset);
      setSelectedBorrower(initialBorrower);
      setAssetSearch("");
      setBorrowerSearch("");
      setAssetResults([]);
      setBorrowerResults([]);
      setReturnAt("");
      setSelectedReturnHours(null);
      setCheckoutError("");
    }
  }, [isOpen]);

  const isAssetSearch = checkoutMode === "borrower" && !selectedAsset;
  const isBorrowerSearch = checkoutMode === "asset" && !selectedBorrower;

  useEffect(() => {
    if (!isOpen || (!isAssetSearch && !isBorrowerSearch)) return;

    const query = (isAssetSearch ? assetSearch : borrowerSearch).trim();
    const setResults = isAssetSearch ? setAssetResults : setBorrowerResults;

    if (query.length < 2) {
      setResults([]);
      return;
    }

    const fetchResults = async () => {
      try {
        setIsSearching(true);
        setCheckoutError("");

        const response = await axiosClient.get(
          isAssetSearch ? "/assets" : "/users",
          { params: { query, page: 0, size: 10 } },
        );
        setResults(response.data?.content ?? response.data ?? []);
      } catch (error) {
        console.error("Search failed:", error);
        setCheckoutError("Unable to load search results.");
      } finally {
        setIsSearching(false);
      }
    };

    fetchResults();
  }, [
    isOpen,
    isAssetSearch,
    isBorrowerSearch,
    assetSearch,
    borrowerSearch,
  ]);

  if (!isOpen) return null;

  const handleReturnTimeSelect = (hours) => {
    const returnDate = new Date();
    returnDate.setHours(returnDate.getHours() + hours);
    setSelectedReturnHours(hours);
    setReturnAt(getLocalDateTimeValue(returnDate));
  };

  const handleCheckout = async (event) => {
    event.preventDefault();
    setCheckoutError("");

    const assetId = selectedAsset?.id ?? selectedAsset?.assetId;
    const borrowerId = selectedBorrower?.id ?? selectedBorrower?.userId;

    if (!assetId || !borrowerId) {
      setCheckoutError("Valid asset and borrower selections are required.");
      return;
    }

    const durationSeconds = Math.ceil(
      (new Date(returnAt).getTime() - Date.now()) / 1000,
    );

    if (durationSeconds <= 0) {
      setCheckoutError("Select a return date and time in the future.");
      return;
    }

    setIsSubmitting(true);

    try {
      await axiosClient.post(`/assets/${assetId}/checkout`, {
        borrowerId: Number(borrowerId),
        checkoutDuration: `PT${durationSeconds}S`,
      });

      onCheckoutSuccess?.();
      notifySuccess("Asset checked out successfully.");
      onClose();
    } catch (error) {
      const message =
        error?.response?.data?.message || "Unable to check out this asset.";
      setCheckoutError(message);
      notifyError(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/10 p-4"
      role="dialog"
      aria-modal="true"
    >
      <div className="w-full max-w-md rounded-lg bg-white p-5 shadow-xl">
        {/* Modal header */}
        <div className="flex items-start justify-between">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Check Out Asset</h2>
            <p className="mt-1 text-sm text-slate-500">
              Select asset, borrower, and return schedule.
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            disabled={isSubmitting}
            className="text-xl leading-none text-slate-400 hover:text-slate-700"
          >
            ×
          </button>
        </div>

        <form onSubmit={handleCheckout} className="mt-5 space-y-4">
          {/* Asset selection */}
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Asset
            </label>
            {selectedAsset ? (
              <div className="flex items-center justify-between rounded-md bg-slate-100 px-3 py-2 text-sm">
                <span>
                  {selectedAsset.name} ({selectedAsset.assetTag || selectedAsset.id})
                </span>
                {checkoutMode === "borrower" && (
                  <button
                    type="button"
                    onClick={() => setSelectedAsset(null)}
                    className="text-indigo-600 hover:text-indigo-800 font-medium"
                  >
                    Change
                  </button>
                )}
              </div>
            ) : (
              <>
                <input
                  type="search"
                  value={assetSearch}
                  onChange={(e) => {
                    setAssetSearch(e.target.value);
                    setAssetResults([]);
                    setCheckoutError("");
                  }}
                  placeholder="Search by tag or name"
                  className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                />
                {assetResults.length > 0 && (
                  <div className="mt-1 max-h-36 overflow-y-auto rounded-md border border-slate-200 bg-white">
                    {assetResults.map((asset) => (
                      <button
                        key={asset.id || asset.assetId}
                        type="button"
                        disabled={asset.status !== "AVAILABLE"}
                        onClick={() => {
                          setSelectedAsset(asset);
                          setAssetResults([]);
                        }}
                        className={`flex w-full items-center justify-between gap-3 border-b border-slate-100 px-3 py-2 text-left text-sm last:border-b-0 ${
                          asset.status === "AVAILABLE"
                            ? "hover:bg-slate-50"
                            : "cursor-not-allowed bg-slate-50 text-slate-400"
                        }`}
                      >
                        <span className="truncate">
                          {asset.name} ({asset.assetTag || asset.id})
                        </span>
                        <span
                          className={`shrink-0 rounded px-2 py-0.5 text-xs font-medium ${
                            asset.status === "AVAILABLE"
                              ? "bg-emerald-100 text-emerald-800"
                              : "bg-slate-200 text-slate-600"
                          }`}
                        >
                          {asset.status?.replaceAll("_", " ") || "Status unknown"}
                        </span>
                      </button>
                    ))}
                  </div>
                )}
              </>
            )}
          </div>

          {/* Borrower selection */}
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Borrower
            </label>
            {selectedBorrower ? (
              <div className="flex items-center justify-between rounded-md bg-slate-100 px-3 py-2 text-sm">
                <div className="flex min-w-0 items-center gap-2">
                  <span className="truncate font-medium">
                    {selectedBorrower.name ||
                      `${selectedBorrower.firstName || ""} ${
                        selectedBorrower.lastName || ""
                      }`.trim() ||
                      selectedBorrower.email}
                  </span>
                  {selectedBorrower.email && (
                    <span className="shrink-0 rounded bg-white px-1.5 py-0.5 text-xs text-slate-500">
                      {selectedBorrower.email}
                    </span>
                  )}
                </div>
                {checkoutMode === "asset" && (
                  <button
                    type="button"
                    onClick={() => setSelectedBorrower(null)}
                    className="text-indigo-600 hover:text-indigo-800 font-medium"
                  >
                    Change
                  </button>
                )}
              </div>
            ) : (
              <>
                <input
                  type="search"
                  value={borrowerSearch}
                  onChange={(e) => {
                    setBorrowerSearch(e.target.value);
                    setBorrowerResults([]);
                    setCheckoutError("");
                  }}
                  placeholder="Search by name or email"
                  className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                />
                {borrowerResults.length > 0 && (
                  <div className="mt-1 max-h-36 overflow-y-auto rounded-md border border-slate-200 bg-white">
                    {borrowerResults.map((borrower) => (
                      <button
                        key={borrower.id || borrower.userId}
                        type="button"
                        onClick={() => {
                          setSelectedBorrower(borrower);
                          setBorrowerResults([]);
                        }}
                        className="flex w-full items-center justify-between gap-3 px-3 py-2 text-left text-sm hover:bg-slate-50"
                      >
                        <span className="truncate font-medium">
                          {borrower.name ||
                            `${borrower.firstName || ""} ${
                              borrower.lastName || ""
                            }`.trim() ||
                            borrower.email}
                        </span>
                        {borrower.email && (
                          <span className="shrink-0 rounded bg-slate-100 px-1.5 py-0.5 text-xs text-slate-500">
                            {borrower.email}
                          </span>
                        )}
                      </button>
                    ))}
                  </div>
                )}
              </>
            )}
          </div>

          {/* Return schedule */}
          <div>
            <label className="mb-2 block text-sm font-medium text-slate-700">
              Return date and time
            </label>
            <div className="grid grid-cols-2 gap-2">
              {returnTimeOptions.map((opt) => (
                <button
                  key={opt.hours}
                  type="button"
                  onClick={() => handleReturnTimeSelect(opt.hours)}
                  className={`rounded-md border px-3 py-2 text-sm ${
                    selectedReturnHours === opt.hours
                      ? "border-indigo-600 bg-indigo-50 text-indigo-700 font-medium"
                      : "border-slate-300 text-slate-700 hover:bg-slate-50"
                  }`}
                >
                  {opt.label}
                </button>
              ))}
            </div>

            <input
              type="datetime-local"
              value={returnAt}
              min={getLocalDateTimeValue(new Date())}
              onChange={(e) => {
                setReturnAt(e.target.value);
                setSelectedReturnHours(null);
              }}
              required
              className="mt-3 w-full rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
            />
          </div>

          {/* Search status and checkout errors */}
          {isSearching && <p className="text-sm text-slate-500">Searching...</p>}

          {checkoutError && (
            <p className="rounded-md bg-rose-50 p-2 text-sm text-rose-700 border border-rose-200">
              {checkoutError}
            </p>
          )}

          {/* Modal actions */}
          <div className="flex justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="rounded-md border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={
                isSubmitting || !selectedAsset || !selectedBorrower || !returnAt
              }
              className="rounded-md bg-indigo-600 px-3 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isSubmitting ? "Checking Out..." : "Confirm Checkout"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CheckoutModal;