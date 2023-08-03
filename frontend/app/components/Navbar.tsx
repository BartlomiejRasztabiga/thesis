import { Link, NavLink, useLocation } from "@remix-run/react";

export default function Navbar() {
  return (
    <header className="flex items-center justify-between bg-slate-800 p-4 text-white">
      <div className="flex items-start">
        <NavLink to="/restaurants" className={({ isActive }) => `text-2xl mr-4 ${isActive ? "font-bold" : ""}`}>
          <h1>Restaurants</h1>
        </NavLink>
        <NavLink to="/settings" className={({ isActive }) => `text-2xl mr-4 ${isActive ? "font-bold" : ""}`}>
          <h2>Settings</h2>
        </NavLink>
      </div>
      <button>
        <Link to="/auth/logout">Logout</Link>
      </button>
    </header>
  );
}
