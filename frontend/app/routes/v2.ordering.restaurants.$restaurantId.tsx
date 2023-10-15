import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getRestaurant } from "~/models/restaurant.server";
import { useLoaderData, useNavigate } from "@remix-run/react";
import invariant from "tiny-invariant";
import { getOrder } from "~/models/order.server";
import { getOrderId } from "~/services/session.server";
import { getCurrentUser } from "~/models/user.server";
import DeliveryDiningIcon from "@mui/icons-material/DeliveryDining";
import Paper from "@mui/material/Paper";
import { Button, IconButton, Typography } from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CardContent from "@mui/material/CardContent";
import Card from "@mui/material/Card";
import { Box } from "@mui/system";
import CardMedia from "@mui/material/CardMedia";
import AddIcon from '@mui/icons-material/Add';


export async function loader({ request, params }: LoaderArgs) {
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
  const navigate = useNavigate();

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <nav className="flex flex-col items-start justify-between w-full py-4 ml-4">
          <button onClick={
            () => {
              navigate("/v2/ordering/restaurants");
            }
          }>
            <ArrowBackIcon fontSize={"large"} />
          </button>
          <hr className="w-full" />
        </nav>
      </div>
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto">
          <img
            src={data.restaurant.imageUrl}
          />
          <div>
            <h5 className="text-lg font-bold">{data.restaurant.name}</h5>
            <p><DeliveryDiningIcon /> ~{data.restaurant.deliveryFee.toFixed(2)} PLN</p>
          </div>
          <hr className="w-full" />
          <div>
            {data.restaurant.menu.map((menuItem) => {
              return (
                <Card sx={{ display: "flex" }} className="my-4">
                  <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <CardContent sx={{ flex: "1 0 auto" }}>
                      <Typography component="div" variant="h5">
                        {menuItem.name}
                      </Typography>
                      <Typography variant="subtitle1" color="text.secondary" component="div">
                        {menuItem.description}
                      </Typography>
                    </CardContent>
                    <Box sx={{ display: "flex", alignItems: "center", pl: 1, pb: 1 }}>
                      <IconButton>
                        <AddIcon fontSize="large" />
                      </IconButton>
                    </Box>
                  </Box>
                  <CardMedia
                    component="img"
                    sx={{ width: "10rem" }}
                    image={menuItem.imageUrl}
                  />
                </Card>
              );
            })}
          </div>
        </Paper>
      </div>
      <div>
        <nav className="flex flex-col items-center justify-between w-full fixed" style={{ bottom: "1rem" }}>
          <Button variant="contained">VIEW BASKET</Button>
        </nav>
      </div>
    </div>
  );
}
