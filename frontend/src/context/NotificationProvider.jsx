import { useEffect, useState } from "react";
import NotificationContext from "./NotificationContext";

export function NotificationProvider({ children }) {
  const [notification, setNotification] = useState(null);

  useEffect(() => {
    if (!notification) return;

    const timeoutId = window.setTimeout(() => setNotification(null), 4000);
    return () => window.clearTimeout(timeoutId);
  }, [notification]);

  const notifySuccess = (message) => {
    setNotification({ message, type: "success" });
  };

  const notifyError = (message) => {
    setNotification({ message, type: "error" });
  };

  const isError = notification?.type === "error";

  return (
    <NotificationContext.Provider value={{ notifySuccess, notifyError }}>
      {children}
      {notification && (
        <div
          className={`success-toast fixed left-1/2 top-4 z-100 flex w-[calc(100%-2rem)] max-w-md -translate-x-1/2 items-center gap-3 rounded-md border px-4 py-3 text-white shadow-lg ${
            isError
              ? "border-rose-800 bg-rose-700"
              : "border-emerald-800 bg-emerald-700"
          }`}
          role={isError ? "alert" : "status"}
          aria-live={isError ? "assertive" : "polite"}
        >
          <span className="flex-none text-sm font-semibold text-white">
            {isError ? "Error" : "Success"}
          </span>
          <p className="flex-1 text-sm text-white">{notification.message}</p>
          <button
            type="button"
            onClick={() => setNotification(null)}
            aria-label="Dismiss notification"
            className="flex-none text-xl leading-none text-white/80 hover:text-white"
          >
            ×
          </button>
        </div>
      )}
    </NotificationContext.Provider>
  );
}