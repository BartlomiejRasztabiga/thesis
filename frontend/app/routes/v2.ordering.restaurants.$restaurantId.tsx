import type { LoaderArgs, ActionArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import { getRestaurant } from "~/models/restaurant.server";
import {
  Form,
  useActionData,
  useFetcher,
  useLoaderData,
  useNavigate,
} from "@remix-run/react";
import invariant from "tiny-invariant";
import {
  addOrderItem,
  cancelOrder,
  deleteOrderItem,
  finalizeOrder,
  getOrder,
  startOrder,
} from "~/models/order.server";
import {
  clearOrderId,
  getOrderId,
  setOrderId,
} from "~/services/session.server";
import { getCurrentUser } from "~/models/user.server";
import {
  DeliveryDining,
  ArrowBack,
  Add,
  Remove,
  ShoppingCart,
  ShoppingBasket,
} from "@mui/icons-material";
import {
  Badge,
  Fab,
  IconButton,
  Typography,
  Paper,
  CardContent,
  Card,
  CardMedia,
} from "@mui/material";
import { Box } from "@mui/system";
import { toast } from "react-toastify";

export async function loader({ request, params }: LoaderArgs) {
  const restaurantId = params.restaurantId;
  invariant(restaurantId, "restaurantId not found");

  const restaurant = await getRestaurant(request, restaurantId);

  let activeOrderId = await getOrderId(request);

  let activeOrder;

  if (activeOrderId) {
    activeOrder = await getOrder(request, activeOrderId);
  } else {
    activeOrderId = (await startOrder(request, restaurantId)).id;
  }

  const currentUser = await getCurrentUser(request);

  return json(
    { restaurant, activeOrder, currentUser },
    {
      headers: {
        // only necessary with cookieSessionStorage
        "Set-Cookie": await setOrderId(request, activeOrderId),
      },
    },
  );
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  try {
    if (_action === "cancel_order") {
      await cancelOrder(request, values.orderId);
      return redirect(`/v2/ordering/restaurants`, {
        headers: {
          "Set-Cookie": await clearOrderId(request),
        },
      });
    }

    if (_action === "add_order_item") {
      const activeOrderId = await getOrderId(request);
      invariant(activeOrderId, "activeOrderId not found");

      const productId = values.productId as string;
      invariant(productId, "productId not found");

      await addOrderItem(request, activeOrderId, productId);

      return json({});
    }

    if (_action === "delete_order_item") {
      const activeOrderId = await getOrderId(request);
      invariant(activeOrderId, "activeOrderId not found");

      const productId = values.productId as string;
      invariant(productId, "productId not found");

      await deleteOrderItem(request, activeOrderId, productId);

      return json({});
    }

    if (_action === "finalize_order") {
      const activeOrderId = await getOrderId(request);
      invariant(activeOrderId, "activeOrderId not found");

      await finalizeOrder(request, activeOrderId);

      return redirect(`/v2/ordering/orders/${activeOrderId}/payment`);
    }
  } catch (e) {
    return json({ error: e.response.data.message });
  }
}

export default function V2RestaurantPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const navigate = useNavigate();

  const fetcher = useFetcher();

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1 });
  }

  const isOrderEmpty =
    !data.activeOrder || Object.keys(data.activeOrder.items).length === 0;

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <nav className="flex flex-col items-start justify-between w-full py-4">
          <button
            className="ml-4"
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
            <ArrowBack fontSize={"large"} />
          </button>
          <hr className="w-full" />
        </nav>
      </div>
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto">
          <img src={data.restaurant.imageUrl} alt="Restaurant's logo" />
          <div>
            <h5 className="text-lg font-bold">{data.restaurant.name}</h5>
            <p>{data.restaurant.location.streetAddress}</p>
            <p>
              <DeliveryDining /> ~{data.restaurant.deliveryFee.toFixed(2)} PLN
            </p>
          </div>
          <hr className="w-full" />
          <div className="mb-16">
            {data.restaurant.menu.map((menuItem, key) => {
              const menuItemCountInActiveOrder =
                data.activeOrder?.items[menuItem.id];

              return (
                <Card sx={{ display: "flex" }} className="my-4" key={key}>
                  <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <CardContent sx={{ flex: "1 0 auto" }}>
                      <Typography component="div" variant="h5">
                        {menuItem.name}
                      </Typography>
                      <Typography
                        variant="subtitle1"
                        color="text.secondary"
                        component="div"
                      >
                        {menuItem.description}
                      </Typography>
                      <Typography
                        variant="subtitle1"
                        color="text.secondary"
                        component="div"
                      >
                        {menuItem.price.toFixed(2)} PLN
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
                      <Form method="post">
                        <input
                          type="hidden"
                          name="productId"
                          value={menuItem.id}
                        />
                        {menuItemCountInActiveOrder && (
                          <>
                            <IconButton
                              type="submit"
                              name="_action"
                              value="delete_order_item"
                            >
                              <Remove fontSize="large" />
                            </IconButton>
                            <IconButton aria-label="cart">
                              <Badge
                                badgeContent={menuItemCountInActiveOrder}
                                color="secondary"
                              >
                                <ShoppingCart />
                              </Badge>
                            </IconButton>
                          </>
                        )}
                        <IconButton
                          type="submit"
                          name="_action"
                          value="add_order_item"
                        >
                          <Add fontSize="large" />
                        </IconButton>
                      </Form>
                    </Box>
                  </Box>
                  {/* TODO make it a rectangle */}
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
        <nav
          className="flex flex-col items-end justify-between fixed"
          style={{ bottom: "1rem", right: "1rem" }}
        >
          <Form method="post">
            <Fab
              variant="extended"
              color="primary"
              type="submit"
              name="_action"
              value="finalize_order"
              disabled={isOrderEmpty}
            >
              <ShoppingBasket className="mr-2" />
              GO TO SUMMARY
            </Fab>
          </Form>
        </nav>
      </div>
    </div>
  );
}
