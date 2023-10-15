import { BottomNavigation, BottomNavigationAction } from "@mui/material";
import HomeIcon from "@mui/icons-material/Home";
import ListIcon from '@mui/icons-material/List';
import SettingsIcon from '@mui/icons-material/Settings';
import LogoutIcon from '@mui/icons-material/Logout';
import { Link } from "@remix-run/react";

export default function BottomNavbar() {
  return (
    <header className="flex items-center justify-between">
      <BottomNavigation
        showLabels
        sx={{ width: "100%" }}
      >
        <BottomNavigationAction label="Home" icon={<HomeIcon />} component={Link} to="/v2/ordering/restaurants" />
        <BottomNavigationAction label="Orders" icon={<ListIcon />} component={Link} to="/v2/ordering/orders" />
        <BottomNavigationAction label="Settings" icon={<SettingsIcon />} component={Link} to="/v2/ordering/settings" />
        <BottomNavigationAction label="Logout" icon={<LogoutIcon />}  component={Link} to="/auth/logout" />
      </BottomNavigation>
    </header>
  )
}
