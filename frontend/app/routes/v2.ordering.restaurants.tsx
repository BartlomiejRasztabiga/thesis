import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import BottomNavbar from "~/components/BottomNavbar";

export async function loader({ request }: LoaderArgs) {
  const restaurants = await getRestaurants(request);
  return json({ restaurants });
}

export default function V2RestaurantsPage() {

  return (
    <div className="flex flex-col h-full">
      <div className="h-full">
        test
      </div>
      <div className="justify-end">
        <hr />
        <BottomNavbar />
      </div>
    </div>
  );
}
