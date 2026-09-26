import { useCallback, useEffect, useState } from "react";
import { axiosClient } from "../api/axiosClient";
import { useAuth } from "../context/AuthContext";
import KpiCard from "../components/KpiCard";
import AssetCatalog from "../components/dashboard/AssetCatalog";
import PatronLookup from "../components/dashboard/PatronLookup";

function Dashboard() {
  const { accessToken, loading: authLoading } = useAuth();

  const [activeTab, setActiveTab] = useState("CATALOG");
  const [searchQuery, setSearchQuery] = useState("");

  const [counts, setCounts] = useState({
    totalCount: 0,
    availableCount: 0,
    checkedOutCount: 0,
    damagedCount: 0,
  });

  const [kpiLoading, setKpiLoading] = useState(true);

  const refreshAssetStats = useCallback(async () => {
    try {
      const response = await axiosClient.get("/assets/stats");
      if (response.data) {
        setCounts({
          totalCount: response.data.totalCount,
          availableCount: response.data.availableCount,
          checkedOutCount: response.data.checkedOutCount,
          damagedCount: response.data.damagedCount,
        });
      }
    } catch (err) {
      console.error("Failed to load KPI stats:", err);
    } finally {
      setKpiLoading(false);
    }
  }, []);

  useEffect(() => {
    if (authLoading || !accessToken) return;

    refreshAssetStats();
  }, [accessToken, authLoading, refreshAssetStats]);

  return (
    <div className="dashboard max-w-7xl mx-auto w-full px-4 py-6 flex flex-col gap-6">
      <div className="grid grid-cols-4 gap-4">
        <KpiCard
          label="Total Assets"
          count={kpiLoading ? "..." : counts.totalCount}
          colorClass="text-slate-900"
        />
        <KpiCard
          label="Available"
          count={kpiLoading ? "..." : counts.availableCount}
          colorClass="text-emerald-600"
        />
        <KpiCard
          label="Checked Out"
          count={kpiLoading ? "..." : counts.checkedOutCount}
          colorClass="text-amber-600"
        />
        <KpiCard
          label="Damaged / Repair"
          count={kpiLoading ? "..." : counts.damagedCount}
          colorClass="text-rose-600"
        />
      </div>

      <div className="flex flex-row justify-between items-center bg-white border border-slate-200 p-4 rounded-xl shadow-sm gap-4">
        <div className="flex gap-4 border-b border-slate-100 pb-2 w-full">
          <button
            className={`text-sm font-semibold pb-1 transition-all ${
              activeTab === "CATALOG"
                ? "text-indigo-600 border-b-2 border-indigo-600"
                : "text-slate-500 hover:text-slate-700"
            }`}
            onClick={() => {
              setActiveTab("CATALOG");
              setSearchQuery("");
            }}
          >
            Available Catalog
          </button>
          <button
            className={`text-sm font-semibold pb-1 transition-all ${
              activeTab === "PATRON-LOOKUP"
                ? "text-indigo-600 border-b-2 border-indigo-600"
                : "text-slate-500 hover:text-slate-700"
            }`}
            onClick={() => {
              setActiveTab("PATRON-LOOKUP");
              setSearchQuery("");
            }}
          >
            Patron Look Up
          </button>
        </div>

        <div className="w-full sm:w-72">
          <input
            className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 transition"
            type="text"
            placeholder={
              activeTab === "CATALOG"
                ? "Search by tag, name or brand"
                : "Search by name or email"
            }
            value={searchQuery}
            onChange={(e) => {
              setSearchQuery(e.target.value);
            }}
          />
        </div>
      </div>

      <div className="workspace-content">
        {activeTab === "CATALOG" ? (
          <AssetCatalog
            query={searchQuery}
            onAssetCirculationChange={refreshAssetStats}
          />
        ) : (
          <PatronLookup
            query={searchQuery}
            onCheckoutSuccess={refreshAssetStats}
          />
        )}
      </div>
    </div>
  );
}
export default Dashboard;
