import type {V2_MetaFunction} from "@remix-run/node";
import {Form, Link} from "@remix-run/react";

import {useOptionalUser} from "~/utils";

export const meta: V2_MetaFunction = () => [{title: "Remix Notes"}];

export default function Index() {
    const user = useOptionalUser();
    return (
        <main className="relative min-h-screen bg-white sm:flex sm:items-center sm:justify-center">
            <div className="relative sm:pb-16 sm:pt-8">
                <div className="mx-auto max-w-7xl sm:px-6 lg:px-8">
                    <div className="relative shadow-xl sm:overflow-hidden sm:rounded-2xl">
                        <div className="relative p-16">
                            {user ? (
                                <div
                                    className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-2 sm:gap-5 sm:space-y-0">
                                    <Link
                                        to="/notes"
                                        className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                                    >
                                        View Notes
                                    </Link>
                                    <Link
                                        to="/restaurants"
                                        className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                                    >
                                        View Restaurants
                                    </Link>
                                </div>
                            ) : (
                                <div
                                    className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-2 sm:gap-5 sm:space-y-0">
                                    <Form action="/auth/auth0?screen_hint=signup" method="post"
                                          className="flex items-center justify-center rounded-md bg-gray-100 px-4 py-3 font-medium text-yellow-700 hover:bg-yellow-50"
                                    >
                                        <button>Sign up</button>
                                    </Form>
                                    <Form action="/auth/auth0" method="post"
                                          className="flex items-center justify-center rounded-md bg-yellow-500 px-4 py-3 font-medium text-white hover:bg-yellow-600"
                                    >
                                        <button>Log In</button>
                                    </Form>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </main>
    );
}
