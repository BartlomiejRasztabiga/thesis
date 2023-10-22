import React from "react";
import type { PayeeResponse } from "~/models/payment.server";
import { RestaurantResponse } from "~/models/restaurant.server";
import { Button } from "@mui/material";
import { Form } from "@remix-run/react";

export default function Topbar(props: TopbarProps) {
  return (
    <header className="flex flex-col items-center justify-between color bg-slate-800 h-10 text-white">
      <Form method="post">
      <span className="align-middle inline-block">
          {props.restaurant.availability}
        </span>
        <input type="hidden" name="availability" value={props.restaurant.availability} />
        <input type="hidden" name="restaurantId" value={props.restaurant.id} />
        <Button type="submit" name="_action" value="update_availability">
          CHANGE
        </Button>
      </Form>
    </header>
  );
}

export interface TopbarProps {
  payee: PayeeResponse;
  restaurant: RestaurantResponse;
}
