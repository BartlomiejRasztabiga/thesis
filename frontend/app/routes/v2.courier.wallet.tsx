import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import BottomNavbar from "~/components/courier/BottomNavbar";
import { Form, useActionData, useLoaderData } from "@remix-run/react";
import {
  Button,
  FormControl,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { getCurrentPayee, withdrawBalance } from "~/models/payment.server";
import { toast } from "react-toastify";

export async function loader({ request }: LoaderArgs) {
  const payee = await getCurrentPayee(request);
  return json({ payee });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  try {
    await withdrawBalance(
      request,
      values.payeeId,
      values.amount,
      values.accountNumber,
    );
  } catch (e) {
    return json({ error: e.response.data.message });
  }
  return json({});
}

export default function V2CourierWalletPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const withdrawalsSorted = data.payee.withdrawals.sort((a, b) => {
    return new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime();
  });

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1 });
  }

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto items-center">
          <div className="flex flex-col items-center">
            <Typography variant="h5">WALLET BALANCE</Typography>
            <Typography variant="subtitle1" color="text.secondary">
              {data.payee.balance} PLN
            </Typography>
          </div>
          <hr className="w-full" />
          <div className="mt-8 mb-8">
            <Form method="post">
              <input type="hidden" name="payeeId" value={data.payee.id} />
              <FormControl>
                <TextField
                  name="amount"
                  label="Amount"
                  variant="outlined"
                  type="number"
                />
                <TextField
                  name="accountNumber"
                  label="Account number"
                  variant="outlined"
                />
                <Button variant="contained" type="submit">
                  Withdraw
                </Button>
              </FormControl>
            </Form>
          </div>
          <hr className="w-full" />
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Date</TableCell>
                  <TableCell>Amount</TableCell>
                  <TableCell>Account number</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {withdrawalsSorted.map((withdrawal, key) => (
                  <TableRow
                    key={key}
                    sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                  >
                    <TableCell component="th" scope="row">
                      {new Date(withdrawal.timestamp).toLocaleString("pl-PL")}
                    </TableCell>
                    <TableCell>{withdrawal.amount.toFixed(2)} PLN</TableCell>
                    <TableCell>{withdrawal.accountNumber}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
