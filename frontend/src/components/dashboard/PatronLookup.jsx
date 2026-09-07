import PatronCard from "./PatronCard";
import { useEffect, useState } from "react";
import { axiosClient } from "../../api/axiosClient";
import { useAuth } from "../../context/AuthContext";

function PatronLookup({ query = "" }) {
  const { accessToken, loading: authLoading } = useAuth();
  const [page, setCurrentPage] = useState(0);

  const [patrons, setPatrons] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);

  const pageSize = 10;

  useEffect(() => {
    setCurrentPage(0);
  }, [query]);

  useEffect(() => {
    if (authLoading || !accessToken) return;

    const fetchPatrons = async () => {
      try {
        setLoading(true);

        const params = new URLSearchParams();
        params.append("page", page);
        params.append("size", pageSize);
        if (query.trim()) {
          params.append("query", query.trim());
        }

        const response = await axiosClient.get(`/users?${params.toString()}`);
        const data = response.data;

        if (data && Array.isArray(data.content)) {
          setPatrons(data.content);

          const pageInfo = data.page || data;
          setTotalPages(pageInfo.totalPages || 1);
          setTotalElements(pageInfo.totalElements || 0);
        } else if (Array.isArray(data)) {
          setPatrons(data);
          setTotalPages(1);
          setTotalElements(data.length);
        } else {
          setPatrons([]);
        }
      } catch (err) {
        console.error("Failed to fetch patron catalog:", err);
        setPatrons([]);
      } finally {
        setLoading(false);
      }
    };

    fetchPatrons();
  }, [accessToken, authLoading, page, query]);

  return (
    <div className="patron-lookup-workspace bg-white border border-slate-200 rounded-xl p-5 shadow-sm flex flex-col gap-5">
      <div className="patron-grid grid grid-cols-5 gap-3.5 min-h-50 h-125 content-start overflow-y-auto">
        {loading ? (
          <div>Loading</div>
        ) : patrons.length > 0 ? (
          patrons.map((patron) => (
            <PatronCard key={patron.id || patron.email} patron={patron} />
          ))
        ) : (
          <div className="col-span-full border border-dashed border-slate-300 rounded-xl p-12 text-center text-slate-500 text-sm">
            No users found matching criteria.
          </div>
        )}
      </div>

      <div className="flex items-center justify-between pt-4 border-t border-slate-100">
        <span className="text-xs text-slate-500">
          Page <span className="font-bold text-slate-900">{page + 1}</span> of{" "}
          <span className="font-bold text-slate-900">{totalPages}</span> (
          {totalElements} patrons)
        </span>

        <div className="flex gap-2">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 0))}
            disabled={page === 0 || loading}
            className="px-3 py-1.5 text-xs font-medium rounded-md border border-slate-300 bg-white hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition"
          >
            Previous
          </button>
          <button
            onClick={() =>
              setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1))
            }
            disabled={page >= totalPages - 1 || loading}
            className="px-3 py-1.5 text-xs font-medium rounded-md border border-slate-300 bg-white hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed transition"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}

export default PatronLookup;
