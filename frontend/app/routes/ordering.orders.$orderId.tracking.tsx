import { useLoaderData, useRevalidator } from "@remix-run/react";
import React, { useEffect } from "react";
import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import Navbar from "~/components/Navbar";
import type { OrderResponse } from "~/models/order.server";
import { getOrder } from "~/models/order.server";
import invariant from "tiny-invariant";
import GoogleMapReact from "google-map-react";

export function Marker(props: any) {
  return <div>{props.text}</div>;
}

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

  const defaultProps = {
    center: {
      lat: 52.2297,
      lng: 21.0122
    },
    zoom: 11
  };

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
                <div style={{ height: "75vh", width: "100%" }}>
                  <GoogleMapReact
                    bootstrapURLKeys={{ key: data.gmapsApiKey }}
                    defaultCenter={defaultProps.center}
                    defaultZoom={defaultProps.zoom}
                  >
                    <Marker
                      lat={52.2297}
                      lng={21.0122}
                      text="My Marker"
                    />
                  </GoogleMapReact>
                </div>
              </div>
            </div>
            <div className="h-full w-80 border-r bg-gray-50"></div>
          </div>
        </div>
      </main>
    </div>
  );
}
