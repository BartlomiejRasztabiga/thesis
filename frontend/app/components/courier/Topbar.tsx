import React from "react";
import type { PayeeResponse } from "~/models/payment.server";
import { Form } from "@remix-run/react";
import { Button } from "@mui/material";
import { CourierResponse } from "~/models/delivery.server";

export default function Topbar(props: TopbarProps) {
  return (
    <header className="flex flex-col items-center justify-between color bg-slate-800 h-10 text-white">
      <Form method="post">
        <span className="align-middle inline-block">
          {props.courier.availability}
        </span>
        <input type="hidden" name="availability" value={props.courier.availability} />
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
