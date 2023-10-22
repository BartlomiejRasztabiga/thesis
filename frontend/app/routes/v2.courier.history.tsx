import type { LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
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
import { getAllDeliveries, getCurrentCourier } from "~/models/delivery.server";

export async function loader({ request, params }: LoaderArgs) {
  const courier = await getCurrentCourier(request);
  const payee = await getCurrentPayee(request);
  const deliveries = await getAllDeliveries(request);

  return json({ courier, payee, deliveries });
}

export default function V2CourierHistoryPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const deliveries = data.deliveries
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
                  <TableCell>Date</TableCell>
                  <TableCell>Reward</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {deliveries.map((delivery, key) => (
                  <TableRow
                    key={key}
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <TableCell component="th" scope="row">
                      {delivery.id}
                    </TableCell>
                    <TableCell>
                      {new Date(delivery.createdAt).toLocaleString("pl-PL")}
                    </TableCell>
                    <TableCell>
                      {delivery.courierFee.toFixed(2)} PLN
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
