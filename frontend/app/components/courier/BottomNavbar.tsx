import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import {
  DeliveryDining,
  List,
  Logout,
  Settings,
  Wallet,
} from "@mui/icons-material";
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
    // TODO replace links!
    <nav className="flex flex-col items-center justify-between w-full fixed bottom-0">
      <hr className="w-full" />
      <BottomNavigation showLabels className="w-full">
        <BottomNavigationAction
          label="Deliver"
          icon={<DeliveryDining />}
          component={Link}
          to="/v2/courier/delivery"
          style={boldIfMatchesPath("/v2/courier/delivery")}
        />
        {/*TODO change icon*/}
        <BottomNavigationAction
          label="History"
          icon={<List />}
          component={Link}
          to="/v2/courier/history"
          style={boldIfMatchesPath("/v2/courier/history")}
        />
        <BottomNavigationAction
          label="Earnings"
          icon={<Wallet />}
          component={Link}
          to="/v2/courier/wallet"
          style={boldIfMatchesPath("/v2/courier/wallet")}
        />
        <BottomNavigationAction
          label="Settings"
          icon={<Settings />}
          component={Link}
          to="/v2/courier/settings"
          style={boldIfMatchesPath("/v2/courier/settings")}
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
