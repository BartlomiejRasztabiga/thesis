import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import BottomNavbar from "~/components/user/BottomNavbar";
import Topbar from "~/components/user/Topbar";
import { NavLink, useLoaderData } from "@remix-run/react";
import { Card, CardMedia, CardContent, CardActionArea } from "@mui/material";
import { StarRate, DeliveryDining } from "@mui/icons-material";
import { getCurrentUser } from "~/models/user.server";

export async function loader({ request }: LoaderArgs) {
  const restaurants = await getRestaurants(request);
  const currentUser = await getCurrentUser(request);
  return json({ restaurants, currentUser });
}

export default function V2RestaurantsPage() {
  const data = useLoaderData<typeof loader>();

  const openRestaurants = data.restaurants.filter(
    (restaurant) => restaurant.availability === "OPEN",
  );

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <Topbar user={data.currentUser} />
      </div>
      <div className="h-full">
        <div className="flex flex-col w-80 mx-auto">
          {openRestaurants.map((restaurant, key) => (
            <Card key={key} className={"my-4"}>
              <CardActionArea>
                <NavLink to={restaurant.id}>
                  <CardMedia
                    component="img"
                    height="194"
                    image={restaurant.imageUrl}
                  />
                  <CardContent>
                    <div className="flex flex-row w-full">
                      <div className="flex-col w-full">
                        <h5 className="text-lg font-bold">{restaurant.name}</h5>
                        <p>
                          <DeliveryDining /> ~
                          {restaurant.deliveryFee.toFixed(2)} PLN
                        </p>
                      </div>
                      <div className="flex-col" style={{ textAlign: "right" }}>
                        <h5>&nbsp;</h5>
                        <p>
                          <StarRate /> {restaurant.avgRating.toFixed(2)}
                        </p>
                      </div>
                    </div>
                  </CardContent>
                </NavLink>
              </CardActionArea>
            </Card>
          ))}
        </div>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
