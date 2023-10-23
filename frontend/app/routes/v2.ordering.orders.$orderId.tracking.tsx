import type { LoaderArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import { useLoaderData, useRevalidator } from "@remix-run/react";
import invariant from "tiny-invariant";
import type { OrderResponse } from "~/models/order.server";
import { getOrder } from "~/models/order.server";
import { Paper } from "@mui/material";
import { useEffect } from "react";
import { ClientOnly } from "remix-utils/client-only";
import { MapClient } from "~/components/Map.client";
import { clearOrderId } from "~/services/session.server";
import {
  Timeline,
  TimelineConnector,
  TimelineContent,
  TimelineDot,
  TimelineItem,
  TimelineOppositeContent,
  TimelineSeparator
} from "@mui/lab";

export async function loader({ request, params }: LoaderArgs) {
  const activeOrderId = params.orderId;
  invariant(activeOrderId, "activeOrderId not found");

  const order = await getOrder(request, activeOrderId);
  invariant(order, "order not found");

  if (order.status === "DELIVERED") {
    return redirect(`/v2/ordering/orders/${order.id}/rating`, {
      headers: {
        "Set-Cookie": await clearOrderId(request)
      }
    });
  }

  return json({ order, gmapsApiKey: process.env.GMAPS_API_KEY });
}

export default function V2OrderTrackingPage() {
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

  const confirmedEvent = data.order.events.find((e) => e.type === "CONFIRMED");
  const courierAssignedEvent = data.order.events.find((e) => e.type === "COURIER_ASSIGNED");
  const preparedEvent = data.order.events.find((e) => e.type === "PREPARED");
  const pickedUpEvent = data.order.events.find((e) => e.type === "PICKED_UP");
  const deliveredEvent = data.order.events.find((e) => e.type === "DELIVERED");

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
          <div>
            <Timeline>
              <TimelineItem>
                <TimelineOppositeContent color="text.secondary">
                  {confirmedEvent && new Date(confirmedEvent.createdAt).toLocaleTimeString("pl-PL")}
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color={confirmedEvent ? "success" : "grey"}
                               variant={confirmedEvent ? "filled" : "outlined"} />
                  <TimelineConnector />
                </TimelineSeparator>
                <TimelineContent>Restaurant has confirmed your order</TimelineContent>
              </TimelineItem>
              <TimelineItem>
                <TimelineOppositeContent color="text.secondary">
                  {courierAssignedEvent && new Date(courierAssignedEvent.createdAt).toLocaleTimeString("pl-PL")}
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color={courierAssignedEvent ? "success" : "grey"}
                               variant={courierAssignedEvent ? "filled" : "outlined"} />
                  <TimelineConnector />
                </TimelineSeparator>
                <TimelineContent>Courier has been assigned</TimelineContent>
              </TimelineItem>
              <TimelineItem>
                <TimelineOppositeContent color="text.secondary">
                  {preparedEvent && new Date(preparedEvent.createdAt).toLocaleTimeString("pl-PL")}
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color={preparedEvent ? "success" : "grey"}
                               variant={preparedEvent ? "filled" : "outlined"} />
                  <TimelineConnector />
                </TimelineSeparator>
                <TimelineContent>Restaurant has prepared your order</TimelineContent>
              </TimelineItem>
              <TimelineItem>
                <TimelineOppositeContent color="text.secondary">
                  {pickedUpEvent && new Date(pickedUpEvent.createdAt).toLocaleTimeString("pl-PL")}
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color={pickedUpEvent ? "success" : "grey"}
                               variant={pickedUpEvent ? "filled" : "outlined"} />
                  <TimelineConnector />
                </TimelineSeparator>
                <TimelineContent>Courier has picked up your order</TimelineContent>
              </TimelineItem>
              <TimelineItem>
                <TimelineOppositeContent color="text.secondary">
                  {deliveredEvent && new Date(deliveredEvent.createdAt).toLocaleTimeString("pl-PL")}
                </TimelineOppositeContent>
                <TimelineSeparator>
                  <TimelineDot color={deliveredEvent ? "success" : "grey"}
                               variant={deliveredEvent ? "filled" : "outlined"} />
                </TimelineSeparator>
                <TimelineContent>Courier has delivered your order</TimelineContent>
              </TimelineItem>
            </Timeline>
          </div>
        </Paper>
      </div>
    </div>
  );
}
