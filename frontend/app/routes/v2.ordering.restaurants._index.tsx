import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import Topbar from "~/components/user/Topbar";
import { NavLink, useLoaderData, useNavigate } from "@remix-run/react";
import { Card, CardActionArea, CardContent, CardMedia, Fab } from "@mui/material";
import { DeliveryDining, StarRate } from "@mui/icons-material";
import { getCurrentUser } from "~/models/user.server";
import { getOrderId } from "~/services/session.server";
import { getOrder } from "~/models/order.server";
import BottomNavbar from "~/components/user/BottomNavbar";

export async function loader({ request }: LoaderArgs) {
  const restaurants = await getRestaurants(request);
  const currentUser = await getCurrentUser(request);

  const activeOrderId = await getOrderId(request);
  const activeOrder = activeOrderId ? await getOrder(request, activeOrderId) : null;

  return json({ restaurants, currentUser, activeOrder });
}

export default function V2RestaurantsPage() {
  const data = useLoaderData<typeof loader>();
  const navigate = useNavigate();

  const openRestaurants = data.restaurants.filter(
    (restaurant) => restaurant.availability === "OPEN"
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
        <nav
          className="flex flex-col items-end justify-between fixed"
          style={{ bottom: "4rem", right: "1rem" }}
        >
          {data.activeOrder && (
            <Fab
              variant="extended"
              color="primary"
              onClick={() => {
                console.log(data.activeOrder)

                if (["CANCELED", "FINALIZED", "REJECTED"].includes(data.activeOrder.status)) {
                  return;
                }

                if (data.activeOrder.status === "CREATED") {
                  navigate(`/v2/ordering/restaurants/${data.activeOrder.restaurantId}`);
                } else {
                  navigate(`/v2/ordering/orders/${data.activeOrder.id}/tracking`);
                }
              }
              }
            >
              GO TO ACTIVE ORDER
            </Fab>
          )}
        < /nav>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
