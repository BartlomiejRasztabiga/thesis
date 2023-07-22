import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { Form, useLoaderData } from "@remix-run/react";
import invariant from "tiny-invariant";
import { getRestaurant } from "~/models/restaurant.server";
import { getOrderId } from "~/services/session.server";
import { getOrder, startOrder } from "~/models/order.server";

export async function loader({ request, params }: LoaderArgs) {
  const restaurantId = params.restaurantId;
  invariant(restaurantId, "restaurantId not found");

  const restaurant = await getRestaurant(request, restaurantId);

  if (!restaurant) {
    throw new Response("Not Found", { status: 404 });
  }

  // TODO load any active order, get id from session

  const activeOrderId = await getOrderId(request);

  let order;

  if (activeOrderId) {
    order = await getOrder(request, activeOrderId);
    console.log(order);
  }

  return json({ restaurant, order });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  invariant(params.restaurantId, "restaurantId not found");

  if (_action === "start_order") {
    console.log("start_order");
    // TODO create order, save id to session

    const id = await startOrder(request, "TODO userId", params.restaurantId);

    return json({
      orderId: id,
    });
  }

  if (_action === "add_to_order") {
    console.log("add_to_order");
    // TODO add item to order

    const productId = values.id;
    console.log(productId);

    return json({});
  }
}

export default function RestaurantPage() {
  const data = useLoaderData<typeof loader>();

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
                  className="rounded bg-blue-500  px-4 py-2 text-white hover:bg-blue-600 focus:bg-blue-400"
                  name="_action"
                  value="add_to_order"
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
        <Form method="post">
          <button
            type="submit"
            className="rounded bg-green-500  px-4 py-2 text-white hover:bg-green-600 focus:bg-green-400"
            name="_action"
            value="start_order"
          >
            Start order
          </button>
        </Form>
      </div>
    </div>
  );
}
