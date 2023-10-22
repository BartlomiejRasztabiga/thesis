import { LocationOn } from "@mui/icons-material";
import type { UserResponse } from "~/models/user.server";
import React from "react";
import { NavLink } from "@remix-run/react";

export default function Topbar(props: TopbarProps) {
  const defaultAddress = props.user.deliveryAddresses.find(
    (address) => address.id == props.user.defaultAddressId,
  );

  return (
    <header className="flex flex-col items-center justify-between">
      <span>
        <NavLink to="/v2/ordering/settings">
          <LocationOn />{" "}
          {defaultAddress?.location.streetAddress || "No location selected"}
        </NavLink>
      </span>
      <hr className="w-full" />
    </header>
  );
}

export interface TopbarProps {
  user: UserResponse;
}
