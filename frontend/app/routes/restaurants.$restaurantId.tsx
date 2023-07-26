import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import { Form, useLoaderData } from "@remix-run/react";
import invariant from "tiny-invariant";
import { getRestaurant } from "~/models/restaurant.server";
import { getOrderId, setOrderId } from "~/services/session.server";
import { addOrderItem, deleteOrderItem, getOrder, startOrder } from "~/models/order.server";

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

  return json({ restaurant, activeOrder });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  invariant(params.restaurantId, "restaurantId not found");

  if (_action === "start_order") {
    console.log("start_order");
    const orderId = await startOrder(request, params.restaurantId);
    console.log("order_id: " + orderId.id);
    const setCookie = await setOrderId(request, orderId.id);
    console.log("setCookie: " + setCookie);

    return json({}, {
      headers: {
        // only necessary with cookieSessionStorage
        "Set-Cookie": setCookie
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
                    !data.activeOrder && "opacity-50"
                  }`}
                  name="_action"
                  value="add_order_item"
                  disabled={!data.activeOrder}
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

        {data.activeOrder ? (
          <>
            <p className="text-lg text-center mb-4">Your order</p>
            {data.activeOrder.items.map((item) => {
              const menuItem = data.restaurant.menu.find((menuItem) => menuItem.id === item.productId);
              invariant(menuItem, "menuItem not found");

              orderSum += menuItem.price;

              return (
                <Form method="post">
                  <input type="hidden" name="id" value={item.id} />
                  <div key={item.id} className="flex flex-row items-center justify-between">
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

            {/* TODO center */}
            <Form method="post">
              <button
                type="submit"
                className="rounded bg-green-500 px-4 py-2 text-white hover:bg-green-600 focus:bg-green-400"
                name="_action"
                value="finalize_order"
              >
                Finalize order
              </button>
            </Form>
          </>
        ) : (
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
        )}
      </div>
    </div>
  );
}
