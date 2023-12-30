import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import type { RestaurantOrderResponse } from "~/models/restaurant.server";
import {
  acceptRestaurantOrder,
  getCurrentRestaurant,
  getRestaurantOrders,
  prepareRestaurantOrder,
  rejectRestaurantOrder,
  updateRestaurantAvailability,
} from "~/models/restaurant.server";
import BottomNavbar from "~/components/manager/BottomNavbar";
import {
  Form,
  useActionData,
  useLoaderData,
  useRevalidator,
} from "@remix-run/react";
import invariant from "tiny-invariant";
import { useEffect } from "react";
import { toast } from "react-toastify";
import Topbar from "~/components/manager/Topbar";
import { getCurrentPayee } from "~/models/payment.server";
import {
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";

export async function loader({ request, params }: LoaderArgs) {
  let restaurant;
  try {
    restaurant = await getCurrentRestaurant(request);
  } catch (error) {
    if (error.response.data.code === "NOT_FOUND") {
      return redirect("/v2/restaurants/setup");
    } else {
      throw error;
    }
  }

  const payee = await getCurrentPayee(request);

  const orders = await getRestaurantOrders(request, restaurant.id);

  return json({ restaurant, orders, payee });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  const restaurantOrderId = values.restaurantOrderId as string;

  const restaurantId = values.restaurantId as string;
  invariant(restaurantId, "restaurantId not found");

  try {
    if (_action === "accept") {
      invariant(restaurantOrderId, "restaurantOrderId not found");
      await acceptRestaurantOrder(request, restaurantId, restaurantOrderId);
    }

    if (_action === "reject") {
      invariant(restaurantOrderId, "restaurantOrderId not found");
      await rejectRestaurantOrder(request, restaurantId, restaurantOrderId);
    }

    if (_action === "prepare") {
      invariant(restaurantOrderId, "restaurantOrderId not found");
      await prepareRestaurantOrder(request, restaurantId, restaurantOrderId);
    }

    if (_action === "update_availability") {
      const currentAvailability = values.availability as string;
      const newAvailability =
        currentAvailability === "OPEN" ? "CLOSED" : "OPEN";

      await updateRestaurantAvailability(
        request,
        restaurantId,
        newAvailability,
      );
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
    ["NEW", "ACCEPTED", "PREPARED"].includes(order.status),
  );

  useEffect(() => {
    const timer = setInterval(() => {
      revalidator.revalidate();
    }, 5000);
    return () => {
      clearInterval(timer);
    };
  }, [revalidator]);

  const getActionButtons = (order: RestaurantOrderResponse) => {
    switch (order.status) {
      case "NEW":
        return (
          <>
            <Button
              sx={{ marginRight: "1rem" }}
              variant="contained"
              type="submit"
              name="_action"
              value="accept"
            >
              Accept
            </Button>
            <Button
              sx={{ marginRight: "1rem" }}
              variant="contained"
              type="submit"
              name="_action"
              value="reject"
            >
              Reject
            </Button>
          </>
        );
      case "ACCEPTED":
        return (
          <>
            <Button
              sx={{ marginRight: "1rem" }}
              variant="contained"
              type="submit"
              name="_action"
              value="prepare"
            >
              Ready
            </Button>
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
        <Topbar payee={data.payee} restaurant={data.restaurant} />
      </div>
      <div className="h-full">
        <div className="flex flex-col w-full mx-auto">
          <Typography variant="h4" className="my-4">
            Active orders
          </Typography>
          <TableContainer component={Paper}>
            <Table aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>Order ID</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Products</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {activeOrders.map((order, key) => (
                  <TableRow
                    key={key}
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <TableCell component="th" scope="row">
                      {order.orderId}
                    </TableCell>
                    <TableCell>
                      {new Date(order.createdAt).toLocaleString("pl-PL")}
                    </TableCell>
                    <TableCell>
                      {Object.keys(order.items).map((item, key) => {
                        const product = data.restaurant.menu.find(
                          (product) => product.id === item,
                        );
                        if (!product) {
                          return null;
                        }
                        return (
                          <div key={key}>
                            <p>
                              {order.items[item]} x {product.name}
                            </p>
                          </div>
                        );
                      })}
                    </TableCell>
                    <TableCell>{order.status}</TableCell>
                    <TableCell>
                      <Form method="post">
                        <input
                          type="hidden"
                          name="restaurantOrderId"
                          value={order.restaurantOrderId}
                        />
                        <input
                          type="hidden"
                          name="restaurantId"
                          value={data.restaurant.id}
                        />
                        {getActionButtons(order)}
                      </Form>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </div>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
