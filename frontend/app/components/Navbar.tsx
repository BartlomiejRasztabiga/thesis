import { Link, useLocation } from "@remix-run/react";

export default function Navbar() {
  const location = useLocation();

  console.log(location);

  function matchesPath(path: string) {
    return location.pathname.includes(path);
  }

  function boldIfMatchesPath(path: string) {
    return matchesPath(path) ? "font-bold" : "";
  }

  return (
    <header className="flex items-center justify-between bg-slate-800 p-4 text-white">
      <div className="flex items-start">
        <h1 className={`text-2xl mr-4 ${boldIfMatchesPath("restaurants")}`}>
          <Link to="/restaurants">Restaurants</Link>
        </h1>
        <h2 className={`text-2xl ${boldIfMatchesPath("settings")}`}>
          <Link to="/settings">Settings</Link>
        </h2>
      </div>
      <button>
        <Link to="/auth/logout">Logout</Link>
      </button>
    </header>
  );
}
