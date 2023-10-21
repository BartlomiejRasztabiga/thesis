import type { LoaderArgs } from "@remix-run/node";
import { ActionArgs, json, redirect } from "@remix-run/node";
import { getRestaurant } from "~/models/restaurant.server";
import {
  Form,
  useActionData,
  useFetcher,
  useLoaderData,
  useNavigate,
  useRevalidator,
} from "@remix-run/react";
import invariant from "tiny-invariant";
import { cancelOrder, getOrder } from "~/models/order.server";
import { Paper, Fab, Box, CircularProgress } from "@mui/material";
import { Close, ShoppingBasket } from "@mui/icons-material";
import { toast } from "react-toastify";
import { clearOrderId } from "~/services/session.server";
import { useEffect } from "react";

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

  invariant(params.orderId, "orderId not found");

  try {
    if (_action === "cancel_order") {
      await cancelOrder(request, params.orderId);
      return redirect(`/v2/ordering/restaurants`, {
        headers: {
          "Set-Cookie": await clearOrderId(request),
        },
      });
    }

    if (_action === "pay") {
      const paymentSessionUrl = values.payment_session_url as string;
      invariant(paymentSessionUrl, "payment_session_url not found");

      return redirect(paymentSessionUrl);
    }
  } catch (e) {
    console.error(e);
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

  const isPaymentLoading = !data.activeOrder.paymentSessionUrl;

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
            <Close fontSize={"large"} />
          </button>
          <hr className="w-full" />
        </nav>
      </div>
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto">
          <div>
            <h5 className="text-lg font-bold">{data.restaurant.name}</h5>
          </div>
          {Object.keys(data.activeOrder.items).map((item, key) => {
            const menuItem = data.restaurant.menu.find(
              (menuItem) => menuItem.id === item,
            );

            return (
              <Paper key={key} className="flex flex-row mb-4">
                <img src={menuItem.imageUrl} style={{ width: "5rem" }} />
                <div>
                  <h5 className="text-lg font-bold">{menuItem.name}</h5>
                  <p>
                    {data.activeOrder.items[item]} x {menuItem.price.toFixed(2)}{" "}
                    PLN
                  </p>
                </div>
              </Paper>
            );
          })}

          {isPaymentLoading ? (
            <Box className="flex justify-center">
              <CircularProgress />
            </Box>
          ) : (
            <Paper>
              <div className="flex flex-row justify-between">
                <p>Delivery fee</p>
                <p>{data.activeOrder.deliveryFee.toFixed(2)} PLN</p>
              </div>
              <div className="flex flex-row justify-between">
                <p>Total</p>
                <p>{data.activeOrder.total.toFixed(2)} PLN</p>
              </div>
              <div className="flex flex-row justify-between">
                <p>Payment method</p>
                <p>STRIPE</p>
              </div>
              <div className="flex flex-row justify-center">
                <Form method="post">
                  <input
                    type="hidden"
                    name="payment_session_url"
                    value={data.activeOrder.paymentSessionUrl}
                  />
                  <Fab
                    variant="extended"
                    size="medium"
                    color="primary"
                    type="submit"
                    name="_action"
                    value="pay"
                  >
                    <ShoppingBasket className="mr-2" />
                    Pay
                  </Fab>
                </Form>
              </div>
            </Paper>
          )}
        </Paper>
      </div>
    </div>
  );
}
