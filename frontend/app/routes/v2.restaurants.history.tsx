import type { LoaderArgs } from "@remix-run/node";
import { ActionArgs, json } from "@remix-run/node";
import {
  getCurrentRestaurant,
  getRestaurantOrders, updateRestaurantAvailability
} from "~/models/restaurant.server";
import BottomNavbar from "~/components/manager/BottomNavbar";
import { useActionData, useLoaderData } from "@remix-run/react";
import { toast } from "react-toastify";
import Topbar from "~/components/manager/Topbar";
import { getCurrentPayee } from "~/models/payment.server";
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import invariant from "tiny-invariant";

export async function loader({ request, params }: LoaderArgs) {
  const restaurant = await getCurrentRestaurant(request);
  const payee = await getCurrentPayee(request);
  const orders = await getRestaurantOrders(request, restaurant.id);

  return json({ restaurant, orders, payee });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  const restaurantId = values.restaurantId as string;
  invariant(restaurantId, "restaurantId not found");

  try {
    if (_action === "update_availability") {
      const currentAvailability = values.availability as string;
      const newAvailability = currentAvailability === "OPEN" ? "CLOSED" : "OPEN";

      await updateRestaurantAvailability(request, restaurantId, newAvailability)
    }
  } catch (e) {
    return json({ error: e.response.data.message });
  }

  return json({});
}

export default function V2RestaurantHistoryPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const historicalOrders = data.orders
    .filter((order) => ["DELIVERED"].includes(order.status))
    .sort((a, b) => {
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    });

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
            Orders history
          </Typography>
          <TableContainer component={Paper}>
            <Table aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>Order ID</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Products</TableCell>
                  <TableCell>Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {historicalOrders.map((order, key) => (
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
