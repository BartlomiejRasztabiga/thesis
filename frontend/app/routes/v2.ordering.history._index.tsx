import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import BottomNavbar from "~/components/user/BottomNavbar";
import { getCurrentUser } from "~/models/user.server";
import { getOrders } from "~/models/order.server";
import { Box, Card, CardContent, CardMedia, IconButton, Typography } from "@mui/material";
import { NavLink, useLoaderData } from "@remix-run/react";
import Topbar from "~/components/user/Topbar";
import { Replay } from "@mui/icons-material";

export async function loader({ request }: LoaderArgs) {
  const orders = await getOrders(request);
  const restaurants = await getRestaurants(request);
  const currentUser = await getCurrentUser(request);
  return json({ orders, restaurants, currentUser });
}

export default function V2OrdersHistoryPage() {
  const data = useLoaderData<typeof loader>();

  const ordersSorted = data.orders.sort((a, b) => {
    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
  });

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <Topbar user={data.currentUser} />
      </div>
      <div className="h-full">
        <div className="flex flex-col w-80 mx-auto">
          <Typography variant="h4" className="my-4">
            Orders history
          </Typography>
          {/*  TODO use mui list instead? */}
          {ordersSorted.map((order, key) => {
            const restaurant = data.restaurants.find(
              (restaurant) => restaurant.id === order.restaurantId
            );

            return (
              <Card sx={{ display: "flex" }} className="my-4" key={key}>
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                  <CardContent sx={{ flex: "1 0 auto" }}>
                    <Typography component="div" variant="h5">
                      {restaurant.name}
                    </Typography>
                    {order.total && (
                      <Typography
                        variant="subtitle2"
                        color="text.secondary"
                        component="div"
                      >
                        {order.total.toFixed(2)} PLN
                      </Typography>
                    )}
                  </CardContent>
                  <Box
                    className="flex"
                  >
                    <Typography
                      variant="subtitle1"
                      color="text.secondary"
                      component="div"
                    >
                      {new Date(order.createdAt).toLocaleString("pl-PL")} {order.status.toLowerCase()}
                    </Typography>
                    <NavLink to={`/v2/ordering/restaurants/${order.restaurantId}`}>
                      <IconButton>
                        <Replay fontSize="large" />
                      </IconButton>
                    </NavLink>
                  </Box>
                </Box>
                <CardMedia
                  component="img"
                  sx={{ width: "6rem" }}
                  image={restaurant.imageUrl}
                />
              </Card>
            );
          })}
        </div>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
