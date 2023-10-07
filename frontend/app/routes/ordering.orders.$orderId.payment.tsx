import { Form, useActionData, useLoaderData } from "@remix-run/react";
import React from "react";
import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import Navbar from "~/components/Navbar";
import { cancelOrder, getOrder } from "~/models/order.server";
import invariant from "tiny-invariant";
import { getRestaurant } from "~/models/restaurant.server";
import { clearOrderId } from "~/services/session.server";

export async function loader({ request, params }: LoaderArgs) {
  // TODO DELETE
  // sleep for 1 second for the payment to be generated
  await new Promise((r) => setTimeout(r, 3000));

  const activeOrderId = params.orderId;
  invariant(activeOrderId, "activeOrderId not found");

  const order = await getOrder(request, activeOrderId);
  invariant(order, "order not found");

  const restaurant = await getRestaurant(request, order.restaurantId);
  invariant(restaurant, "restaurant not found");

  return json({ order, restaurant });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  invariant(params.orderId, "orderId not found");

  if (_action === "pay") {
    const paymentSessionUrl = values.payment_session_url as string;
    invariant(paymentSessionUrl, "payment_session_url not found");

    // redirect to payment gateway
    return redirect(paymentSessionUrl);
  }

  if (_action === "cancel") {
    await cancelOrder(request, params.orderId);

    return redirect(`/ordering/restaurants/`, {
      headers: {
        // only necessary with cookieSessionStorage
        "Set-Cookie": await clearOrderId(request),
      },
    });
  }
}

export default function OrderPaymentPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData<typeof action>();

  // @ts-ignore
  const error = actionData?.error;

  return (
    <div className="flex h-full min-h-screen flex-col">
      <Navbar />

      <main className="flex h-full bg-white">
        <div className="h-full w-80 border-r bg-gray-50">
          <hr />
        </div>

        <div className="flex-1 p-6">
          <div className="flex h-full bg-white">
            <div className="border-r flex-1 mr-2">
              <h3 className="text-2xl font-bold">Your order</h3>
              <hr className="my-4" />
              <div>
                {data.order.items.map((item) => {
                  const menuItem = data.restaurant.menu.find(
                    (menuItem) => menuItem.id === item.productId,
                  );
                  invariant(menuItem, "menuItem not found");

                  return (
                    <div key={item.id}>
                      <p className="flex-grow">
                        {" "}
                        1x {menuItem.name}, {menuItem.price} PLN
                      </p>
                    </div>
                  );
                })}
                <div>
                  <p className="flex-grow">
                    {" "}
                    1x Delivery fee, {data.order.deliveryFee} PLN
                  </p>
                </div>
              </div>
              <hr className="my-4" />
              <div>
                <p className="flex-grow font-bold">Total: {data.order.itemsTotal + data.order.deliveryFee} PLN</p>
              </div>
              <hr className="my-4" />
              <div>
                <p className="flex-grow font-bold">Selected delivery address</p>
                <span>{data.order.deliveryLocation.streetAddress}</span>
              </div>
              <hr className="my-4" />
              {error && (
                <div
                  className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
                  role="alert"
                >
                  <span className="block sm:inline">{error}</span>
                </div>
              )}
              <div>
                <Form method="post">
                  <input
                    type="hidden"
                    name="payment_session_url"
                    value={data.order.paymentSessionUrl}
                  />
                  <button
                    type="submit"
                    className="rounded bg-green-500 px-4 py-2 text-white hover:bg-green-600 focus:bg-green-400"
                    name="_action"
                    value="pay"
                  >
                    Pay
                  </button>
                  <button
                    type="submit"
                    className="rounded bg-red-500 px-4 py-2 text-white hover:bg-red-600 focus:bg-red-400"
                    name="_action"
                    value="cancel"
                  >
                    Cancel
                  </button>
                </Form>
              </div>
            </div>
            <div className="h-full w-80 border-r bg-gray-50"></div>
          </div>
        </div>
      </main>
    </div>
  );
}
