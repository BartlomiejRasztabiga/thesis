import type {ActionArgs, LoaderArgs} from "@remix-run/node";
import {json} from "@remix-run/node";
import {Form, useLoaderData} from "@remix-run/react";
import invariant from "tiny-invariant";
import {getRestaurant} from "~/models/restaurant.server";

export async function loader({request, params}: LoaderArgs) {
    const restaurantId = params.restaurantId;
    invariant(restaurantId, "restaurantId not found");

    const restaurant = await getRestaurant(request, restaurantId);

    if (!restaurant) {
        throw new Response("Not Found", {status: 404});
    }

    return json({restaurant});
}

export async function action({request, params}: ActionArgs) {
    // TODO start order or add to order
}

export default function RestaurantPage() {
    const data = useLoaderData<typeof loader>();

    return (
        <div className="flex h-full bg-white">
            <div className="border-r flex-1 mr-2">
                <h3 className="text-2xl font-bold">{data.restaurant.name}</h3>
                <p className="py-6">{data.restaurant.availability}</p>
                <hr className="my-4"/>
                <div>
                    {data.restaurant.menu.map((item) => (
                        <div key={item.id}>
                            <h3 className="text-2xl font-bold">{item.name}</h3>
                            <p className="py-6">{item.description}</p>
                            <p className="py-6">{item.price}</p>
                            <Form method="post">
                                <button
                                    type="submit"
                                    className="rounded bg-blue-500  px-4 py-2 text-white hover:bg-blue-600 focus:bg-blue-400"
                                >
                                    Add to order
                                </button>
                            </Form>
                            <hr className="my-4"/>
                        </div>
                    ))}
                </div>
            </div>
            <div className="h-full w-80 border-r bg-gray-50">
                Active order details
            </div>
        </div>
    );
}
