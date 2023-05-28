import type {LoaderArgs} from "@remix-run/node";
import {json} from "@remix-run/node";
import {Form, Link, NavLink, Outlet, useLoaderData} from "@remix-run/react";

import {getAccessToken} from "~/services/session.server";
import {useUser} from "~/utils";
import {getRestaurants} from "~/models/restaurant.server";

export async function loader({request}: LoaderArgs) {
    const restaurants = await getRestaurants(request);
    return json({restaurants});
}

export default function RestaurantsPage() {
    const data = useLoaderData<typeof loader>();

    return (
        <div className="flex h-full min-h-screen flex-col">
            <header className="flex items-center justify-between bg-slate-800 p-4 text-white">
                <h1 className="text-3xl font-bold">
                    <Link to=".">Restaurants</Link>
                </h1>
            </header>

            <main className="flex h-full bg-white">
                <div className="h-full w-80 border-r bg-gray-50">
                    <hr/>

                    {data.restaurants.length === 0 ? (
                        <p className="p-4">No restaurants yet</p>
                    ) : (
                        <ol>
                            {data.restaurants.map((restaurant) => (
                                <li key={restaurant.id}>
                                    <NavLink
                                        className={({isActive}) =>
                                            `block border-b p-4 text-xl ${isActive ? "bg-white" : ""}`
                                        }
                                        to={restaurant.id}
                                    >{restaurant.name}
                                    </NavLink>
                                </li>
                            ))}
                        </ol>
                    )}
                </div>

                <div className="flex-1 p-6">
                    <Outlet/>
                </div>
            </main>
        </div>
    );
}
