import type { LoaderArgs } from "@remix-run/node";
import { ActionArgs, json } from "@remix-run/node";
import {
  acceptRestaurantOrder,
  getCurrentRestaurant,
  getRestaurantOrders,
  prepareRestaurantOrder,
  rejectRestaurantOrder,
  RestaurantOrderResponse,
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
} from "@mui/material";

export async function loader({ request, params }: LoaderArgs) {
  const restaurant = await getCurrentRestaurant(request);

  const payee = await getCurrentPayee(request);

  const orders = await getRestaurantOrders(request, restaurant.id);

  return json({ restaurant, orders, payee });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  const restaurantOrderId = values.restaurantOrderId as string;
  invariant(restaurantOrderId, "restaurantOrderId not found");

  const restaurantId = values.restaurantId as string;
  invariant(restaurantId, "restaurantId not found");

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
    ["NEW", "ACCEPTED", "PREPARED"].includes(order.status),
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
        <Topbar payee={data.payee} />
      </div>
      <div className="h-full">
        <div className="flex flex-col w-full mx-auto">
          <TableContainer component={Paper}>
            <Table aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>Order ID</TableCell>
                  <TableCell>Created at</TableCell>
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
