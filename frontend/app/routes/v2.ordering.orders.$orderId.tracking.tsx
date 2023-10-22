import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { useLoaderData, useNavigate, useRevalidator } from "@remix-run/react";
import invariant from "tiny-invariant";
import type { OrderResponse } from "~/models/order.server";
import { getOrder } from "~/models/order.server";
import { Paper } from "@mui/material";
import { useEffect } from "react";
import { ClientOnly } from "remix-utils/client-only";
import { MapClient } from "~/components/Map.client";

export async function loader({ request, params }: LoaderArgs) {
  const activeOrderId = params.orderId;
  invariant(activeOrderId, "activeOrderId not found");

  const order = await getOrder(request, activeOrderId);
  invariant(order, "order not found");

  return json({ order, gmapsApiKey: process.env.GMAPS_API_KEY });
}

export default function V2OrderTrackingPage() {
  const data = useLoaderData<typeof loader>();

  const revalidator = useRevalidator();
  const navigate = useNavigate();

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

  if (data.order.status == "DELIVERED") {
    setTimeout(() => {
      navigate(`/v2/ordering/orders/${data.order.id}/rating`);
    }, 5000);
  }

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div className="h-full">
        <Paper className="flex flex-col w-full mx-auto">
          <div>
            <h5 className="text-lg font-bold">Order tracking</h5>
            <p className="text-sm text-gray-500">Order #{data.order.id}</p>
            <p className="text-gray-500">{getOrderSummary(data.order)}</p>
          </div>
          <ClientOnly
            fallback={<div style={{ height: mapHeight }}>loading...</div>}
          >
            {() => (
              <MapClient
                height={mapHeight}
                restaurantLocation={data.order.restaurantLocation}
                deliveryLocation={data.order.deliveryLocation}
                courierLocation={data.order.courierLocation}
              />
            )}
          </ClientOnly>
          <div>TODO add timeline like on bolt food?</div>
        </Paper>
      </div>
    </div>
  );
}
