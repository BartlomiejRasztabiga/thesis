import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { Form, Link, useLoaderData, useRevalidator } from "@remix-run/react";
import invariant from "tiny-invariant";
import type { RestaurantOrderResponse } from "~/models/restaurant.server";
import {
  acceptRestaurantOrder,
  getRestaurant,
  getRestaurantOrders,
  prepareRestaurantOrder,
  rejectRestaurantOrder,
} from "~/models/restaurant.server";
import React, { useEffect } from "react";

export async function loader({ request, params }: LoaderArgs) {
  const restaurantId = params.restaurantId;
  invariant(restaurantId, "restaurantId not found");

  const restaurant = await getRestaurant(request, restaurantId);

  if (!restaurant) {
    throw new Response("Not Found", { status: 404 });
  }

  const orders = await getRestaurantOrders(request, restaurantId);

  // TODO load orders

  return json({ restaurant, orders });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  const restaurantId = params.restaurantId;

  invariant(restaurantId, "restaurantId not found");

  const restaurantOrderId = values.restaurantOrderId as string;

  invariant(restaurantOrderId, "restaurantOrderId not found");

  if (_action === "accept") {
    await acceptRestaurantOrder(request, restaurantId, restaurantOrderId);
  }

  if (_action === "reject") {
    await rejectRestaurantOrder(request, restaurantId, restaurantOrderId);
  }

  if (_action === "prepare") {
    await prepareRestaurantOrder(request, restaurantId, restaurantOrderId);
  }

  return json({});
}

export default function RestaurantManagerPage() {
  const data = useLoaderData<typeof loader>();

  const revalidator = useRevalidator();

  const activeOrders = data.orders.filter((order) =>
    ["NEW", "ACCEPTED", "PREPARED"].includes(order.status),
  );

  // TODO good enough for now
  useEffect(() => {
    const timer = setInterval(() => {
      revalidator.revalidate();
    }, 5000);
    return () => {
      clearInterval(timer);
    };
  }, [revalidator]);

  const getActionButtons = (order: RestaurantOrderResponse) => {
    const className =
      "rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 focus:bg-blue-400 mx-2";

    switch (order.status) {
      case "NEW":
        return (
          <>
            <button
              type="submit"
              className={className}
              name="_action"
              value="accept"
            >
              Accept
            </button>
            <button
              type="submit"
              className={className}
              name="_action"
              value="reject"
            >
              Reject
            </button>
          </>
        );
      case "ACCEPTED":
        return (
          <>
            <button
              type="submit"
              className={className}
              name="_action"
              value="prepare"
            >
              Ready
            </button>
          </>
        );

      default:
        return null;
    }
  };

  return (
    <div className="flex h-full min-h-screen flex-col">
      <header className="flex items-center justify-between bg-slate-800 p-4 text-white">
        <div className="flex items-start text-2xl mr-4 font-bold">
          <h1>{data.restaurant.name}</h1>
        </div>
        <button>
          <Link to="/auth/logout">Logout</Link>
        </button>
      </header>

      <main className="flex h-full bg-white">
        <div className="flex-1 p-6">
          <p className="py-6">{data.restaurant.availability}</p>
          {/* TODO update availability */}
          <hr className="my-4" />
          <div>
            {/* TODO table */}
            <div className="overflow-x-auto">
              <table className="table table-zebra">
                {/* head */}
                <thead>
                  <tr>
                    <th>Order ID</th>
                    <th>Products</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {activeOrders.map((order) => (
                    <tr key={order.restaurantOrderId}>
                      <th>{order.restaurantOrderId}</th>
                      <th>
                        {order.items.map((item, key) => {
                          const product = data.restaurant.menu.find(
                            (product) => product.id === item.productId,
                          );
                          if (!product) {
                            return null;
                          }
                          return (
                            <div key={key}>
                              <p>{product.name}</p>
                            </div>
                          );
                        })}
                      </th>
                      <th>{order.status}</th>
                      <th>
                        <Form method="post">
                          <input
                            type="hidden"
                            name="restaurantOrderId"
                            value={order.restaurantOrderId}
                          />
                          {getActionButtons(order)}
                        </Form>
                      </th>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
