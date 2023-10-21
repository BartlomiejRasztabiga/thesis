import type { LoaderArgs } from "@remix-run/node";
import { ActionArgs, json } from "@remix-run/node";
import {
  acceptRestaurantOrder,
  getRestaurant,
  getRestaurantOrders,
  prepareRestaurantOrder,
  rejectRestaurantOrder,
  RestaurantOrderResponse
} from "~/models/restaurant.server";
import BottomNavbar from "~/components/manager/BottomNavbar";
import { useActionData, useLoaderData, useRevalidator } from "@remix-run/react";
import invariant from "tiny-invariant";
import { useEffect } from "react";
import { toast } from "react-toastify";
import Topbar from "~/components/manager/Topbar";

export async function loader({ request, params }: LoaderArgs) {
  const restaurantId = params.restaurantId;
  invariant(restaurantId, "restaurantId not found");

  const restaurant = await getRestaurant(request, restaurantId);

  if (!restaurant) {
    throw new Response("Not Found", { status: 404 });
  }

  const orders = await getRestaurantOrders(request, restaurantId);

  return json({ restaurant, orders });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  const restaurantId = params.restaurantId;

  invariant(restaurantId, "restaurantId not found");

  const restaurantOrderId = values.restaurantOrderId as string;

  invariant(restaurantOrderId, "restaurantOrderId not found");

  try {
    if (_action === "accept") {
      await acceptRestaurantOrder(request, restaurantId, restaurantOrderId);
    }

    if (_action === "reject") {
      await rejectRestaurantOrder(request, restaurantId, restaurantOrderId);
    }

    if (_action === "prepare") {
      await prepareRestaurantOrder(request, restaurantId, restaurantOrderId);
    }
  } catch (e) {
    return json({ error: e.response.data.message });
  }

  return json({});
}

export default function V2RestaurantPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const revalidator = useRevalidator();

  const activeOrders = data.orders.filter((order) =>
    ["NEW", "ACCEPTED", "PREPARED"].includes(order.status)
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
    // TODO change to MUI buttons!!!
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

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1 });
  }

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <Topbar />
      </div>
      <div className="h-full">
        <div className="flex flex-col w-80 mx-auto">
          {/*  TODO MUI TABLE */}
        </div>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
