import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import ListIcon from "@mui/icons-material/List";
import SettingsIcon from "@mui/icons-material/Settings";
import LogoutIcon from "@mui/icons-material/Logout";
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
    <header className="flex items-center justify-between">
      <hr />
      <BottomNavigation
        showLabels
        sx={{ width: "100%", position: "fixed", bottom: 0 }}
      >
        <BottomNavigationAction
          label="Home"
          icon={<HomeIcon />}
          component={Link}
          to="/v2/ordering/restaurants"
          style={boldIfMatchesPath("/v2/ordering/restaurants")}
        />
        <BottomNavigationAction
          label="Orders"
          icon={<ListIcon />}
          component={Link}
          to="/v2/ordering/orders"
          style={boldIfMatchesPath("/v2/ordering/orders")}
        />
        <BottomNavigationAction
          label="Settings"
          icon={<SettingsIcon />}
          component={Link}
          to="/v2/ordering/settings"
          style={boldIfMatchesPath("/v2/ordering/settings")}
        />
        <BottomNavigationAction
          label="Logout"
          icon={<LogoutIcon />}
          component={Link}
          to="/auth/logout"
        />
      </BottomNavigation>
    </header>
  );
}
