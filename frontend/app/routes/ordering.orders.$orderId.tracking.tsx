import { useLoaderData, useRevalidator } from "@remix-run/react";
import React, { Suspense, useEffect } from "react";
import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import Navbar from "~/components/Navbar";
import type { OrderResponse } from "~/models/order.server";
import { getOrder } from "~/models/order.server";
import invariant from "tiny-invariant";
import { MapClient } from "~/components/Map.client";

export async function loader({ request, params }: LoaderArgs) {
  const activeOrderId = params.orderId;
  invariant(activeOrderId, "activeOrderId not found");

  const order = await getOrder(request, activeOrderId);
  invariant(order, "order not found");

  return json({ order, gmapsApiKey: process.env.GMAPS_API_KEY });
}

export default function OrderTrackingPage() {
  const data = useLoaderData<typeof loader>();

  const revalidator = useRevalidator();

  // TODO good enough for now
  useEffect(() => {
    const timer = setInterval(() => {
      revalidator.revalidate();
    }, 5000);
    return () => {
      clearInterval(timer);
    };
  }, [revalidator]);

  // TODO live update order status

  const getOrderSummary = (order: OrderResponse): string => {
    switch (order.status) {
      case "FINALIZED":
        return "Waiting for payment to be processed...";
      case "PAID":
        return "Restaurant has seen your order...";
      case "CONFIRMED":
        return "Your order is being prepared...";
      case "REJECTED":
        return "Restaurant has rejected your order. Full refund will be issued shortly.";
      case "PREPARED":
        return "Your order is ready for pickup!";
      case "PICKED_UP":
        return "Your order is on its way!";
      case "DELIVERED":
        return "Your order has been delivered!";
      default:
        return "";
    }
  };

  const mapHeight = "75vh";

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
                <h4 className="text-xl font-bold">Order #{data.order.id}</h4>
                <p className="text-gray-500">{getOrderSummary(data.order)}</p>
              </div>
              <Suspense
                fallback={
                  <div>loading...</div>
                }
              >
              <MapClient
                height={mapHeight}
                restaurantLocation={data.order.restaurantLocation}
                deliveryLocation={data.order.deliveryLocation}
                courierLocation={data.order.courierLocation}
              />
              </Suspense>
            </div>
            <div className="h-full w-80 border-r bg-gray-50"></div>
          </div>
        </div>
      </main>
    </div>
  );
}
