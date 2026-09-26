import { useEffect, useState } from "react";
import { axiosClient } from "../../api/axiosClient";

function InspectUserModal({ selectedUser, isOpen, onClose }) {
  const [user, setUser] = useState();

  useEffect(() => {
    if (!isOpen) {
      setUser(null);
      return;
    }

    const fetchUser = async () => {
      try {
        const response = await axiosClient.get(`/users/${selectedUser.id}`);
        const data = response.data;

        if (data) {
          setUser(data);
        }
      } catch (err) {
        console.error("Failed to fetch user catalog:", err);
        setUser(null);
      }
    };

    fetchUser();
  }, [isOpen, selectedUser.id]);

  if (!isOpen) return null;
  const patron = user ?? selectedUser;

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
            <h2 className="text-lg font-bold text-slate-900">View Patron</h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Close patron details"
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
                {patron.firstName.charAt(0).toUpperCase()}
              </div>
              <div className="min-w-0">
                <p className="truncate text-base font-semibold text-slate-900">
                  {patron.firstName} {patron.lastName}
                </p>
                <p className="mt-0.5 text-sm text-slate-500">{patron.role}</p>
              </div>
            </div>

            <dl className="mt-6 divide-y divide-slate-100 border-y border-slate-100">
              <div className="py-3">
                <dt className="text-xs font-medium text-slate-500">Email</dt>
                <dd className="mt-1 wrap-break-word text-sm text-slate-800">
                  {patron.email}
                </dd>
              </div>
              <div className="py-3">
                <dt className="text-xs font-medium text-slate-500">User ID</dt>
                <dd className="mt-1 text-sm text-slate-800">{patron.id}</dd>
              </div>
              <div className="py-3">
                <dt className="text-xs font-medium text-slate-500">Total Items Checked Out</dt>
                <dd className="mt-1 text-sm text-slate-800">PLACE HOLDER</dd>
              </div>
            </dl>
          </section>

          <section className="min-w-0 p-4">{/* 60% column */}</section>
        </div>
      </div>
    </div>
  );
}

export default InspectUserModal;
