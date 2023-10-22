import type { LoaderArgs } from "@remix-run/node";
import { ActionArgs, json, redirect } from "@remix-run/node";
import { Form, NavLink, useLoaderData } from "@remix-run/react";
import invariant from "tiny-invariant";
import { getOrder, rateOrder } from "~/models/order.server";
import { Button, Paper, Rating } from "@mui/material";
import { useState } from "react";

export async function loader({ request, params }: LoaderArgs) {
  const activeOrderId = params.orderId;
  invariant(activeOrderId, "activeOrderId not found");

  const order = await getOrder(request, activeOrderId);
  invariant(order, "order not found");

  return json({ order });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...
    values } = Object.fromEntries(formData);

  invariant(params.orderId, "orderId not found");
  invariant(values.rating, "rating not found")

  try {
    await rateOrder(request, params.orderId, values.rating);
    return redirect(`/v2/ordering/history`);
  } catch (e) {
    return json({ error: e.response.data.message });
  }
}

export default function V2OrderRatingPage() {
  const data = useLoaderData<typeof loader>();

  const [rating, setRating] = useState(0);

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto items-center">
          <h5 className="text-lg">Your order has been delivered</h5>
          <p className="text-sm text-gray-500">
            Please rate your experience with the restaurant
          </p>
          <Rating name="rating" size="large" className="my-2" value={rating}
                  onChange={(event, newValue) => setRating(newValue)} />
          <div className="flex">
            <Form method="post">
              <input type="hidden" name="rating" value={rating} />
              <Button type="submit" variant="contained">
                Submit
              </Button>
              <Button>
                <span className="text-sm">
                  <NavLink to="/v2/ordering/history">Skip</NavLink>
                </span>
              </Button>
            </Form>
          </div>
        </Paper>
      </div>
    </div>
  );
}
