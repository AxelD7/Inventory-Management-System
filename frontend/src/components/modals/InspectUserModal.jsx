import { useEffect, useState } from "react";
import { axiosClient } from "../../api/axiosClient";

function InspectUserModal({ selectedUser, isOpen, onClose }) {
  const [assets, setAssets] = useState([]);
  const [assetsLoading, setAssetsLoading] = useState(false);
  const [isCheckingIn, setIsCheckingIn] = useState(false);
  const [checkinAssetId, setCheckinAssetId] = useState(null);
  const [isDamaged, setIsDamaged] = useState(false);
  const [checkinNotes, setCheckinNotes] = useState("");
  const [checkinError, setCheckinError] = useState("");

  useEffect(() => {
    if (!isOpen || !selectedUser?.id) return;

    const fetchAssets = async () => {
      try {
        setAssetsLoading(true);
        const status = "ACTIVE";
        const response = await axiosClient.get(
          `/users/${selectedUser.id}/circulations?status=${status}`,
        );

        const data = response.data;
        setAssets(Array.isArray(data) ? data : []);
      } catch (err) {
        if (err.name !== "CanceledError") {
          console.error("Failed to fetch asset catalog:", err);
          setAssets([]);
        }
      } finally {
        setAssetsLoading(false);
      }
    };

    fetchAssets();
  }, [isOpen, selectedUser?.id]);

  const openCheckinForm = (assetId) => {
    setCheckinAssetId(assetId);
    setIsDamaged(false);
    setCheckinNotes("");
    setCheckinError("");
  };

  const handleCheckin = async (assetId) => {
    try {
      setIsCheckingIn(true);
      setCheckinError("");
      await axiosClient.post(`/assets/${assetId}/checkin`, {
        isDamaged,
        notes: checkinNotes,
      });
      setAssets((currentAssets) =>
        currentAssets.filter((asset) => asset.id !== assetId),
      );
      setCheckinAssetId(null);
    } catch (err) {
      console.error("Failed to check in asset:", err);
      setCheckinError("Could not check in this asset. Please try again.");
    } finally {
      setIsCheckingIn(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/10 p-4"
      role="dialog"
      aria-modal="true"
    >
      <div className="content flex h-[min(30rem,90vh)] w-full max-w-5xl flex-col rounded-lg bg-white p-5 shadow-2xl">
        {/* Modal header */}
        <div className="flex items-start justify-between">
          <div>
            <h2 className="text-lg font-bold text-slate-900">View user</h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close user details"
            className="text-xl leading-none text-slate-400 hover:text-slate-700"
          >
            ×
          </button>
        </div>

        <div className="mt-5 grid flex-1 grid-cols-1 divide-y divide-slate-200 md:grid-cols-[40%_60%] md:divide-x md:divide-y-0">
          <section className="min-w-0 p-4 md:pl-0">
            <h3 className="text-xs font-semibold uppercase tracking-wide text-slate-500">
              User information
            </h3>
            <div className="mt-4 flex items-center gap-3">
              <div className="flex size-12 shrink-0 items-center justify-center rounded-full bg-indigo-50 text-lg font-semibold text-indigo-700">
                {selectedUser.firstName.charAt(0).toUpperCase()}
              </div>
              <div className="min-w-0">
                <p className="truncate text-base font-semibold text-slate-900">
                  {selectedUser.firstName} {selectedUser.lastName}
                </p>
                <p className="mt-0.5 text-sm text-slate-500">
                  {selectedUser.role}
                </p>
              </div>
            </div>

            <dl className="mt-6 divide-y divide-slate-100 border-y border-slate-100">
              <div className="py-3">
                <dt className="text-xs font-medium text-slate-500">Email</dt>
                <dd className="mt-1 wrap-break-word text-sm text-slate-800">
                  {selectedUser.email}
                </dd>
              </div>
              <div className="py-3">
                <dt className="text-xs font-medium text-slate-500">User ID</dt>
                <dd className="mt-1 text-sm text-slate-800">
                  {selectedUser.id}
                </dd>
              </div>
              <div className="py-3">
                <dt className="text-xs font-medium text-slate-500">
                  Total Items Checked Out
                </dt>
                <dd className="mt-1 text-sm text-slate-800">{assets.length}</dd>
              </div>
            </dl>
          </section>

          <section className="flex min-h-0 min-w-0 flex-col p-4 md:pr-0">
            <div className="flex items-center justify-between gap-3">
              <h3 className="text-xs font-semibold uppercase tracking-wide text-slate-500">
                Checked-out assets
              </h3>
              <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-semibold text-slate-600">
                {assets.length}
              </span>
            </div>

            <div className="mt-3 min-h-0 flex-1 space-y-2 overflow-y-auto pr-1">
              {checkinError && (
                <p role="alert" className="rounded-md bg-rose-50 px-3 py-2 text-sm text-rose-700">
                  {checkinError}
                </p>
              )}
              {assetsLoading ? (
                <p className="py-8 text-center text-sm text-slate-500">
                  Loading assets...
                </p>
              ) : assets.length > 0 ? (
                assets.map((asset) => (
                  <article
                    key={asset.id}
                    className="rounded-md border border-slate-200 px-3 py-2.5"
                  >
                    <div className="flex items-center justify-between gap-3">
                      <div className="min-w-0">
                        <p className="truncate text-sm font-semibold text-slate-800">
                          {asset.name}
                        </p>
                        <p className="mt-0.5 truncate text-xs text-slate-500">
                          {[asset.assetTag, asset.brand].filter(Boolean).join(" · ")}
                        </p>
                      </div>
                      <div className="flex shrink-0 items-center gap-2">
                        <span className="rounded bg-amber-50 px-2 py-1 text-xs font-medium text-amber-800">
                          {asset.status?.replaceAll("_", " ") || "Checked out"}
                        </span>
                        <button
                          type="button"
                          onClick={() => openCheckinForm(asset.id)}
                          disabled={isCheckingIn}
                          className="rounded bg-emerald-700 px-2.5 py-1.5 text-xs font-semibold text-white hover:bg-emerald-800 disabled:cursor-wait disabled:opacity-60"
                        >
                          Check in
                        </button>
                      </div>
                    </div>
                    {checkinAssetId === asset.id && (
                      <form
                        className="mt-3 space-y-3 border-t border-slate-100 pt-3"
                        onSubmit={(event) => {
                          event.preventDefault();
                          handleCheckin(asset.id);
                        }}
                      >
                        <label className="flex items-center gap-2 text-sm text-slate-700">
                          <input
                            type="checkbox"
                            checked={isDamaged}
                            onChange={(event) => setIsDamaged(event.target.checked)}
                            className="size-4 rounded border-slate-300 text-emerald-700 focus:ring-emerald-700"
                          />
                          Is this asset damaged?
                        </label>
                        {isDamaged && (
                          <textarea
                            value={checkinNotes}
                            onChange={(event) => setCheckinNotes(event.target.value)}
                            rows={2}
                            placeholder="Describe the damage"
                            aria-label="Damage details"
                            className="w-full resize-y rounded-md border border-slate-300 px-3 py-2 text-sm focus:border-emerald-700 focus:outline-none focus:ring-1 focus:ring-emerald-700"
                          />
                        )}
                        <div className="flex justify-end gap-2">
                          <button
                            type="button"
                            onClick={() => setCheckinAssetId(null)}
                            disabled={isCheckingIn}
                            className="rounded border border-slate-300 px-2.5 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-60"
                          >
                            Cancel
                          </button>
                          <button
                            type="submit"
                            disabled={isCheckingIn}
                            className="rounded bg-emerald-700 px-2.5 py-1.5 text-xs font-semibold text-white hover:bg-emerald-800 disabled:cursor-wait disabled:opacity-60"
                          >
                            {isCheckingIn ? "Checking in..." : "Confirm check-in"}
                          </button>
                        </div>
                      </form>
                    )}
                  </article>
                ))
              ) : (
                <p className="rounded-md border border-dashed border-slate-200 px-3 py-8 text-center text-sm text-slate-500">
                  No assets are currently checked out.
                </p>
              )}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}

export default InspectUserModal;
