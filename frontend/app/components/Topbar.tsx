import LocationOnIcon from "@mui/icons-material/LocationOn";
import { UserResponse } from "~/models/user.server";
import React from "react";
import { Divider } from "@mui/material";

export default function Topbar(props: TopbarProps) {
  const defaultAddress = props.user.deliveryAddresses.find((address) => address.id == props.user.defaultAddressId);

  return (
    <header className="flex flex-col items-center justify-between">
      <span><LocationOnIcon /> {defaultAddress?.location.streetAddress || "No location selected"}</span>
      <hr className="w-full" />
    </header>
  );
}

export interface TopbarProps {
  user: UserResponse;
}
