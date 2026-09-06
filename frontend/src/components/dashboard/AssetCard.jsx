import noImg from "../../assets/no-image.svg";

function AssetCard({ asset }) {
  if (!asset) return null;

  const renderStatusBadge = (status) => {
    switch (status) {
      case "AVAILABLE":
        return (
          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-100 text-emerald-800 border border-emerald-200">
            Available
          </span>
        );
      case "CHECKED_OUT":
      case "UNAVAILABLE":
        return (
          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-amber-100 text-amber-800 border border-amber-200">
            Checked Out
          </span>
        );
      case "DAMAGED":
        return (
          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-rose-100 text-rose-800 border border-rose-200">
            Damaged
          </span>
        );
      default:
        return (
          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-slate-100 text-slate-700 border border-slate-200">
            {status || "UNKNOWN"}
          </span>
        );
    }
  };

  return (
    <div className="asset-card bg-white rounded-lg border border-slate-200 p-3 shadow-sm flex flex-col justify-between hover:border-slate-300 transition-all">
      <div className="flex flex-col gap-2">
        <div className="w-full h-24 bg-slate-100 rounded flex items-center justify-center border border-slate-100 overflow-hidden p-2">
          <img
            src={noImg}
            alt={asset.name || "Default asset placeholder"}
            className="w-full h-full object-contain opacity-60"
          />
        </div>

        <div className="flex items-center justify-between">
          <span className="font-mono text-[11px] font-bold text-indigo-600 bg-indigo-50 px-1.5 py-0.5 rounded border border-indigo-100">
            {asset.assetTag}
          </span>
          {renderStatusBadge(asset.status)}
        </div>

        <div>
          <h4
            className="font-bold text-slate-900 text-xs truncate"
            title={asset.name}
          >
            {asset.name}
          </h4>
          <p className="text-[11px] text-slate-500 truncate mt-0.5">
            {asset.brand || "Generic Brand"}
          </p>
        </div>
      </div>

      <div className="pt-2 mt-2 border-t border-slate-100 flex items-center justify-end">
        {asset.status === "AVAILABLE" ? (
          <button className="bg-indigo-600 hover:bg-indigo-700 text-white text-[10px] font-semibold px-2 py-1 rounded transition shadow-sm">
            Checkout
          </button>
        ) : (
          <button
            disabled
            className="bg-slate-100 text-slate-400 text-[10px] font-semibold px-2 py-1 rounded border border-slate-200 cursor-not-allowed"
          >
            Unavailable
          </button>
        )}
      </div>
    </div>
  );
}

export default AssetCard;
