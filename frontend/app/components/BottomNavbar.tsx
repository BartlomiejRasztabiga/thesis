import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import {Home, List, Settings, Logout} from "@mui/icons-material";
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
          label="Home"
          icon={<Home />}
          component={Link}
          to="/v2/ordering/restaurants"
          style={boldIfMatchesPath("/v2/ordering/restaurants")}
        />
        <BottomNavigationAction
          label="Orders"
          icon={<List />}
          component={Link}
          to="/v2/ordering/orders"
          style={boldIfMatchesPath("/v2/ordering/orders")}
        />
        <BottomNavigationAction
          label="Settings"
          icon={<Settings />}
          component={Link}
          to="/v2/ordering/settings"
          style={boldIfMatchesPath("/v2/ordering/settings")}
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
