import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import {Home, List, Settings, Logout, BarChart} from "@mui/icons-material";
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
    // skad wziac restaurantId skoro nie wiemy jakie ma przypisane? moze na razie hardcoded
    // a moze po prostu bez id ???
    <nav className="flex flex-col items-center justify-between w-full fixed bottom-0">
      <hr className="w-full" />
      <BottomNavigation showLabels className="w-full">
        <BottomNavigationAction
          label="Orders"
          icon={<List />}
          component={Link}
          to="/v2/restaurants/5ee006d0-e9d5-4ce7-a431-6cb0653296f9"
          style={boldIfMatchesPath("/v2/ordering/restaurants")}
        />
        {/*TODO change icon*/}
        <BottomNavigationAction
          label="History"
          icon={<List />}
          component={Link}
          to="/v2/restaurants/5ee006d0-e9d5-4ce7-a431-6cb0653296f9"
          style={boldIfMatchesPath("/v2/ordering/orders")}
        />
        {/* TODO settings? */}
        <BottomNavigationAction
          label="Settings"
          icon={<Settings />}
          component={Link}
          to="/v2/restaurants/5ee006d0-e9d5-4ce7-a431-6cb0653296f9/settings"
          style={boldIfMatchesPath("/v2/ordering/settings")}
        />
        {/* TODO statistics? */}
        <BottomNavigationAction
          label="Statistics"
          icon={<Settings />}
          component={BarChart}
          to="/v2/restaurants/5ee006d0-e9d5-4ce7-a431-6cb0653296f9"
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
