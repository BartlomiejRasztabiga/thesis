import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurant } from "~/models/restaurant.server";
import BottomNavbar from "~/components/BottomNavbar";
import Topbar from "~/components/Topbar";
import { useLoaderData } from "@remix-run/react";
import invariant from "tiny-invariant";
import { getOrder } from "~/models/order.server";
import { getOrderId } from "~/services/session.server";
import { getCurrentUser } from "~/models/user.server";

export async function loader({ request, params }: LoaderArgs) {
  console.log("test")

  const restaurantId = params.restaurantId;
  invariant(restaurantId, "restaurantId not found");

  const restaurant = await getRestaurant(request, restaurantId);

  const activeOrderId = await getOrderId(request);

  let activeOrder;

  if (activeOrderId) {
    activeOrder = await getOrder(request, activeOrderId);
  } else {
    // TODO start order
  }

  const currentUser = await getCurrentUser(request);

  return json({ restaurant, activeOrder, currentUser });
}

export default function V2RestaurantPage() {
  const data = useLoaderData<typeof loader>();

  return (
    <div className="flex flex-col h-full">
      <div>
        <Topbar user={data.currentUser} />
      </div>
      <div className="h-full">
        <div className="flex flex-col w-80 mx-auto">
          <div>
            {data.restaurant.name}
          </div>
          <div>
            GO TO BASKET???
          </div>
        </div>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
