import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import BottomNavbar from "~/components/user/BottomNavbar";
import { getCurrentUser } from "~/models/user.server";
import { getOrders } from "~/models/order.server";
import { Box, Card, CardContent, CardMedia, Typography } from "@mui/material";
import { useLoaderData } from "@remix-run/react";
import Topbar from "~/components/user/Topbar";

export async function loader({ request }: LoaderArgs) {
  const orders = await getOrders(request);
  const currentUser = await getCurrentUser(request);
  return json({ orders, currentUser });
}

export default function V2OrdersHistoryPage() {
  const data = useLoaderData<typeof loader>();

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <Topbar user={data.currentUser} />
      </div>
      <div className="h-full">
        <div className="flex flex-col w-80 mx-auto">
          {/*  TODO use list instead? */}
          {data.orders.map((order, key) => {
            return (
              <Card sx={{ display: "flex" }} className="my-4" key={key}>
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                  <CardContent sx={{ flex: "1 0 auto" }}>
                    <Typography component="div" variant="h5">
                      {order.id}
                    </Typography>
                    <Typography
                      variant="subtitle1"
                      color="text.secondary"
                      component="div"
                    >
                      {order.id}
                    </Typography>
                  </CardContent>
                  <Box
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      pl: 1,
                      pb: 1,
                    }}
                  >
                    TEST
                  </Box>
                </Box>
                {/* TODO restaurant's image */}
                {/*<CardMedia*/}
                {/*  component="img"*/}
                {/*  sx={{ width: "10rem" }}*/}
                {/*  image={menuItem.imageUrl}*/}
                {/*/>*/}
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
