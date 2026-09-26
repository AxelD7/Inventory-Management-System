import { useState } from "react";
import noImg from "../../assets/no-image.svg";
import CheckoutModal from "../modals/CheckoutModal";

function PatronCard({ patron, onSelectPatron, onCheckoutSuccess }) {
  const [isCheckoutModalOpen, setIsCheckoutModalOpen] = useState(false);

  if (!patron) return null;

  const handleCheckoutSuccess = () => {
    setIsCheckoutModalOpen(false);
    onCheckoutSuccess?.();
  };

  return (
    <>
      <div
        onClick={() => onSelectPatron && onSelectPatron(patron)}
        className="patron-card bg-white rounded-lg border border-slate-200 p-3 shadow-sm flex flex-col justify-between"
      >
        <div className="card-top flex flex-col gap-2">
          <div className="user-img w-full h-24 bg-slate-100 rounded flex items-center justify-center border border-slate-100 overflow-hidden p-2">
            <img
              src={noImg}
              alt="User placeholder"
              className="w-full h-full object-contain opacity-60"
            />
          </div>

          <div className="badge-row flex items-center justify-between gap-2">
            <span className="email-tag text-[11px] font-bold text-indigo-600 bg-indigo-50 px-1.5 py-0.5 rounded border border-indigo-100">
              {patron.email}
            </span>
          </div>

          <div className="info-block">
            <h4 className="user-name font-bold text-slate-900 text-xs">
              {patron.firstName} {patron.lastName}
            </h4>
            <p className="text-[11px] text-slate-500 mt-0.5">
              {patron.role || "Unknown Role"}
            </p>
          </div>
        </div>

        <div className="pt-2 mt-2 border-t border-slate-100 flex items-center justify-end">
          <button
            type="button"
            onClick={(event) => {
              event.stopPropagation();
              setIsCheckoutModalOpen(true);
            }}
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-[10px] font-semibold px-2 py-1 rounded transition shadow-sm"
          >
            Checkout
          </button>
        </div>
      </div>

      <CheckoutModal
        isOpen={isCheckoutModalOpen}
        checkoutMode="borrower"
        initialBorrower={patron}
        onClose={() => setIsCheckoutModalOpen(false)}
        onCheckoutSuccess={handleCheckoutSuccess}
      />
    </>
  );
}

export default PatronCard;
