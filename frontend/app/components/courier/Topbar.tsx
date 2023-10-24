import React from "react";
import type { PayeeResponse } from "~/models/payment.server";
import { Form } from "@remix-run/react";
import { Button } from "@mui/material";
import type { CourierResponse } from "~/models/delivery.server";

export default function Topbar(props: TopbarProps) {
  return (
    <header className="flex flex-col items-center justify-between color bg-slate-800 h-15 text-white">
      <span>Hi, {props.courier.name}!</span>
      <Form method="post">
        <span className="align-middle inline-block">
          {props.courier.availability}
        </span>
        <input
          type="hidden"
          name="availability"
          value={props.courier.availability}
        />
        <Button type="submit" name="_action" value="update_availability">
          CHANGE
        </Button>
      </Form>
    </header>
  );
}

export interface TopbarProps {
  payee: PayeeResponse;
  courier: CourierResponse;
}
