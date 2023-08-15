import { Link, NavLink } from "@remix-run/react";
import React from "react";

export default function Navbar() {
  return (
    <header className="flex items-center justify-between bg-slate-800 p-4 text-white">
      <div className="flex items-start">
        <NavLink to="/ordering/restaurants" className={({ isActive }) => `text-2xl mr-4 ${isActive ? "font-bold" : ""}`}>
          <h1>Restaurants</h1>
        </NavLink>
        <NavLink to="/ordering/settings" className={({ isActive }) => `text-2xl mr-4 ${isActive ? "font-bold" : ""}`}>
          <h2>Settings</h2>
        </NavLink>
      </div>
      <button>
        <Link to="/auth/logout">Logout</Link>
      </button>
    </header>
  );
}
