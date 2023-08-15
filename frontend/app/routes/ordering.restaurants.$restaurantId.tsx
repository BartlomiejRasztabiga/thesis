import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import { Form, Link, useLoaderData } from "@remix-run/react";
import invariant from "tiny-invariant";
import { getRestaurant } from "~/models/restaurant.server";
import { clearOrderId, getOrderId, setOrderId } from "~/services/session.server";
import { addOrderItem, cancelOrder, deleteOrderItem, finalizeOrder, getOrder, startOrder } from "~/models/order.server";
import { getCurrentUser } from "~/models/user.server";

export async function loader({ request, params }: LoaderArgs) {
  const restaurantId = params.restaurantId;
  invariant(restaurantId, "restaurantId not found");

  const restaurant = await getRestaurant(request, restaurantId);

  if (!restaurant) {
    throw new Response("Not Found", { status: 404 });
  }

  const activeOrderId = await getOrderId(request);

  let activeOrder;

  if (activeOrderId) {
    activeOrder = await getOrder(request, activeOrderId);
  }

  const currentUser = await getCurrentUser(request);

  return json({ restaurant, activeOrder, currentUser });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  invariant(params.restaurantId, "restaurantId not found");

  if (_action === "start_order") {
    const orderId = await startOrder(request, params.restaurantId);

    return json({}, {
      headers: {
        // only necessary with cookieSessionStorage
        "Set-Cookie": await setOrderId(request, orderId.id)
      }
    });
  }

  if (_action === "cancel_order") {
    const activeOrderId = await getOrderId(request);
    invariant(activeOrderId, "activeOrderId not found");

    await cancelOrder(request, activeOrderId);

    return json({}, {
      headers: {
        // only necessary with cookieSessionStorage
        "Set-Cookie": await clearOrderId(request)
      }
    });
  }

  if (_action === "finalize_order") {
    const activeOrderId = await getOrderId(request);
    invariant(activeOrderId, "activeOrderId not found");

    const deliveryAddressId = values.address as string;
    invariant(deliveryAddressId, "deliveryAddressId not found");

    await finalizeOrder(request, activeOrderId, deliveryAddressId);

    return redirect(`/orders/${activeOrderId}/payment`, {
      headers: {
        // only necessary with cookieSessionStorage
        "Set-Cookie": await clearOrderId(request)
      }
    });
  }

  if (_action === "add_order_item") {
    const activeOrderId = await getOrderId(request);
    invariant(activeOrderId, "activeOrderId not found");

    const productId = values.id as string;
    invariant(productId, "productId not found");

    await addOrderItem(request, activeOrderId, productId);

    return json({});
  }

  if (_action === "delete_order_item") {
    const activeOrderId = await getOrderId(request);
    invariant(activeOrderId, "activeOrderId not found");

    const orderItemId = values.id as string;
    invariant(orderItemId, "orderItemId not found");

    await deleteOrderItem(request, activeOrderId, orderItemId);

    return json({});
  }
}

export default function RestaurantPage() {
  const data = useLoaderData<typeof loader>();

  let orderSum = 0;

  let activeOrderRestaurantSelected = data.activeOrder && data.restaurant.id === data.activeOrder.restaurantId;

  const getContent = () => {
    if (!data.activeOrder) return (
      <Form method="post">
        <button
          type="submit"
          className="rounded bg-green-500 px-4 py-2 text-white hover:bg-green-600 focus:bg-green-400"
          name="_action"
          value="start_order"
        >
          Start order
        </button>
      </Form>
    );

    if (!activeOrderRestaurantSelected) return (
      <>
        <p className="text-lg text-center mb-4">You have an active order from another restaurant</p>
        <Link to={`/ordering/restaurants/${data.activeOrder.restaurantId}`}
              className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 focus:bg-blue-400">
          Go to active order
        </Link>
      </>
    );

    if (activeOrderRestaurantSelected) return (
      <>
        <p className="text-lg text-center mb-4">Your order</p>
        {data.activeOrder.items.map((item) => {
          const menuItem = data.restaurant.menu.find((menuItem) => menuItem.id === item.productId);
          invariant(menuItem, "menuItem not found");

          orderSum += menuItem.price;

          return (
            <Form method="post" key={item.id}>
              <input type="hidden" name="id" value={item.id} />
              <div className="flex flex-row items-center justify-between">
                <p className="flex-grow"> 1x {menuItem.name}, {menuItem.price} PLN</p>
                <button
                  type="submit"
                  className="rounded bg-red-500 px-4 py-2 text-white hover:bg-red-600 focus:bg-red-400"
                  name="_action"
                  value="delete_order_item"
                >
                  {/* TODO add proper icons */}
                  🗑
                </button>
                <hr className="my-4" />
              </div>
            </Form>
          );
        })}
        <p className="text-lg text-center">Total: {orderSum} PLN</p>
        <Form method="post">
          <select className="select select-bordered w-full max-w-xs" name="address">
            {data.currentUser.deliveryAddresses.map((address) => (
              <option key={address.id} value={address.id}>{address.address}</option>
            ))}
          </select>
          {/* TODO center */}
          <button
            type="submit"
            className="rounded bg-green-500 px-4 py-2 text-white hover:bg-green-600 focus:bg-green-400"
            name="_action"
            value="finalize_order"
          >
            Finalize order
          </button>
          <button
            type="submit"
            className="rounded bg-red-500 px-4 py-2 text-white hover:bg-red-600 focus:bg-red-400"
            name="_action"
            value="cancel_order"
          >
            Cancel order
          </button>
        </Form>
      </>
    );
  };

  return (
    <div className="flex h-full bg-white">
      <div className="border-r flex-1 mr-2">
        <h3 className="text-2xl font-bold">{data.restaurant.name}</h3>
        <p className="py-6">{data.restaurant.availability}</p>
        <hr className="my-4" />
        <div>
          {data.restaurant.menu.map((item) => (
            <div key={item.id}>
              <h3 className="text-2xl font-bold">{item.name}</h3>
              <p className="py-6">{item.description}</p>
              <p className="py-6">{item.price}</p>
              <Form method="post">
                <input type="hidden" name="id" value={item.id} />
                <button
                  type="submit"
                  className={`rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 focus:bg-blue-400 ${
                    !activeOrderRestaurantSelected && "opacity-50"
                  }`}
                  name="_action"
                  value="add_order_item"
                  disabled={!activeOrderRestaurantSelected}
                >
                  Add to order
                </button>
              </Form>
              <hr className="my-4" />
            </div>
          ))}
        </div>
      </div>
      <div className="h-full w-80 border-r bg-gray-50">
        {getContent()}
      </div>
    </div>
  );
}
