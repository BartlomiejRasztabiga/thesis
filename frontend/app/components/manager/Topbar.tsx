import { LocationOn } from "@mui/icons-material";
import { UserResponse } from "~/models/user.server";
import React from "react";
import { NavLink } from "@remix-run/react";

export default function Topbar(props: TopbarProps) {


  return (
    <header className="flex flex-col items-center justify-between color bg-slate-800 h-10 text-white">
      <span className="align-middle inline-block">
          WALLET BALANCE: PLN
      </span>
      <hr className="w-full" />
    </header>
  );
}

export interface TopbarProps {

}
