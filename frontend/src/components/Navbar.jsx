import { useAuth } from "../context/AuthContext";
import logo from "../assets/brand-nav.svg";
import { Link } from "react-router-dom";

function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="navbar w-full bg-blue-900 border-b border-slate-600 shadow-md">
      <div className="max-w-7xl mx-auto px-5 py-3 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <span className="flex items-center justify-center shrink-0">
            <Link to="/">
              <img src={logo} alt="Brand Logo" className="h-15 w-auto" />
            </Link>
          </span>

          <h1 className="text-slate-100 font-bold text-lg tracking-wide">
            Inventory Management System
          </h1>
        </div>

        {user ? (
          <div className="flex items-center gap-5">
            <div className="flex flex-col text-right">
              <span className="text-sm font-medium text-slate-200">
                {user.firstName} {user.lastName}
              </span>
              <span className="text-[11px] text-indigo-400 capitalize mt-0.5">
                {user.role.toLowerCase()}
              </span>
            </div>

            <div className="flex flex-col gap-2">
              <button className="bg-slate-700 hover:bg-slate-600 text-white text-xs px-3 py-1 rounded transition">
                Profile
              </button>
              <button
                className="bg-rose-600 hover:bg-rose-700 text-white text-xs px-3 py-1 rounded transition"
                onClick={logout}
              >
                Logout
              </button>
            </div>
          </div>
        ) : (
          <Link
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-xs px-3 py-1 rounded transition"
            to="/signin"
          >
            Login
          </Link>
        )}
      </div>
    </header>
  );
}

export default Navbar;
