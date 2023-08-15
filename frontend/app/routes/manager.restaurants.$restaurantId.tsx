import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { Link, useLoaderData } from "@remix-run/react";
import invariant from "tiny-invariant";
import { getRestaurant, getRestaurantOrders } from "~/models/restaurant.server";
import React from "react";

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

}

export default function RestaurantManagerPage() {
  const data = useLoaderData<typeof loader>();

  console.log(data)

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
            {data.orders.map((order) => (
              <div key={order.restaurantOrderId} className="flex items-center justify-between">
                <p>{order.restaurantOrderId}</p>
                <p>{order.status}</p>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}
