function KpiCard({ label, count, colorClass = "text-slate-900" }) {
  return (
    <div className="card-container bg-white rounded-xl border border-slate-200 p-5 shadow-sm flex-1">
      <span className="label-text text-sm font-medium text-slate-500">
        {label}
      </span>
      <div className={`count-number text-2xl font-bold m-1 ${colorClass}`}>
        {count}
      </div>
    </div>
  );
}

export default KpiCard;
