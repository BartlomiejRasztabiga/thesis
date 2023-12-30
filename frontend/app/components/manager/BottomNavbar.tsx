import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import { List, Logout, Settings, Wallet } from "@mui/icons-material";
import { Link, useLocation } from "@remix-run/react";

export default function BottomNavbar() {
  const location = useLocation();

  function matchesPath(path: string) {
    return location.pathname.includes(path);
  }

  function boldIfMatchesPath(path: string) {
    return matchesPath(path) ? { fontWeight: "bold" } : {};
  }

  return (
    <nav className="flex flex-col items-center justify-between w-full fixed bottom-0">
      <hr className="w-full" />
      <BottomNavigation showLabels className="w-full">
        <BottomNavigationAction
          label="Current orders"
          icon={<List />}
          component={Link}
          to="/v2/restaurants/orders"
          style={boldIfMatchesPath("/v2/restaurants/orders")}
        />
        <BottomNavigationAction
          label="History"
          icon={<List />}
          component={Link}
          to="/v2/restaurants/history"
          style={boldIfMatchesPath("/v2/restaurants/history")}
        />
        <BottomNavigationAction
          label="Earnings"
          icon={<Wallet />}
          component={Link}
          to="/v2/restaurants/wallet"
          style={boldIfMatchesPath("/v2/restaurants/wallet")}
        />
        <BottomNavigationAction
          label="Settings"
          icon={<Settings />}
          component={Link}
          to="/v2/restaurants/settings"
          style={boldIfMatchesPath("/v2/restaurants/settings")}
        />
        <BottomNavigationAction
          label="Logout"
          icon={<Logout />}
          component={Link}
          to="/auth/logout"
        />
      </BottomNavigation>
    </nav>
  );
}
