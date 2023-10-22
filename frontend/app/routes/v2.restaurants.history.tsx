import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { getCurrentRestaurant, getRestaurantOrders } from "~/models/restaurant.server";
import BottomNavbar from "~/components/manager/BottomNavbar";
import { Form, useActionData, useLoaderData, useRevalidator } from "@remix-run/react";
import { toast } from "react-toastify";
import Topbar from "~/components/manager/Topbar";
import { getCurrentPayee } from "~/models/payment.server";
import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";

export async function loader({ request, params }: LoaderArgs) {
  const restaurant = await getCurrentRestaurant(request);
  const payee = await getCurrentPayee(request);
  const orders = await getRestaurantOrders(request, restaurant.id);

  return json({ restaurant, orders, payee });
}

export default function V2RestaurantHistoryPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const historicalOrders = data.orders.filter((order) =>
    ["DELIVERED"].includes(order.status)
  ).sort((a, b) => {
    return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
  });

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
          <Typography variant="h4" className="my-4">
            Orders history
          </Typography>
          <TableContainer component={Paper}>
            <Table aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell>Order ID</TableCell>
                  <TableCell>Created at</TableCell>
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
                          (product) => product.id === item
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
