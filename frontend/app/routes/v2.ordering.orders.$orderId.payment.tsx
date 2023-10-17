import type { LoaderArgs } from "@remix-run/node";
import { ActionArgs, json } from "@remix-run/node";
import { getRestaurant } from "~/models/restaurant.server";
import {
  useActionData,
  useFetcher,
  useLoaderData,
  useNavigate, useRevalidator
} from "@remix-run/react";
import invariant from "tiny-invariant";
import { getOrder } from "~/models/order.server";
import Paper from "@mui/material/Paper";
import { Fab } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import ShoppingBasketIcon from "@mui/icons-material/ShoppingBasket";
import { toast } from "react-toastify";
import { useEffect } from "react";
import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';

export async function loader({ request, params }: LoaderArgs) {
  // TODO refresh few times to get stripe url set up and totals
  // TODO https://remix.run/docs/en/main/guides/streaming#3-deferring-data-in-loaders
  // TODO albo revalidator jak w tracking.tsx

  const activeOrderId = params.orderId;
  invariant(activeOrderId, "activeOrderId not found");

  const activeOrder = await getOrder(request, activeOrderId);
  invariant(activeOrder, "activeOrder not found");

  const restaurant = await getRestaurant(request, activeOrder.restaurantId);
  invariant(restaurant, "restaurant not found");

  return json({ activeOrder, restaurant });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  try {
  } catch (e) {
    return json({ error: e.response.data.message });
  }
}

export default function V2OrderPaymentPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const navigate = useNavigate();

  const fetcher = useFetcher();

  const revalidator = useRevalidator();

  useEffect(() => {
    if (data.activeOrder.paymentSessionUrl) return;

    const timer = setInterval(() => {
      revalidator.revalidate();
    }, 1000);
    return () => {
      clearInterval(timer);
    };
  }, [revalidator]);

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1 });
  }

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <nav className="flex flex-col items-start justify-between w-full py-4 ml-4">
          <button
            onClick={() => {
              if (data.activeOrder) {
                fetcher.submit(
                  { _action: "cancel_order", orderId: data.activeOrder.id },
                  { method: "POST" },
                );
              } else {
                navigate("/v2/ordering/restaurants");
              }
            }}
          >
            <CloseIcon fontSize={"large"} />
          </button>
          <hr className="w-full" />
        </nav>
      </div>
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto">
          {/* TODO add loading modal */}
          {!data.activeOrder.paymentSessionUrl && (<Box className="flex"><CircularProgress /></Box>)}
          PAY!!!
        </Paper>
      </div>
      <div>
        <nav
          className="flex flex-col items-end justify-between w-full fixed"
          style={{ bottom: "01rem", right: "1rem" }}
        >
          <Fab variant="extended" color="primary">
            <ShoppingBasketIcon className="mr-2" />
            GO TO SUMMARY
          </Fab>
        </nav>
      </div>
    </div>
  );
}
