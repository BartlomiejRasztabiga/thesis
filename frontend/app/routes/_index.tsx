import type { V2_MetaFunction } from "@remix-run/node";
import { Form, Link } from "@remix-run/react";

import { useOptionalUser } from "~/utils";

export const meta: V2_MetaFunction = () => [{ title: "Thesis" }];

export default function Index() {
  const user = useOptionalUser();

  return (
    <main className="relative min-h-screen bg-white sm:flex sm:items-center sm:justify-center">
      <div className="relative sm:pb-16 sm:pt-8">
        <div className="mx-auto max-w-7xl sm:px-6 lg:px-8">
          <div className="relative shadow-xl sm:overflow-hidden sm:rounded-2xl">
            <div className="relative p-16">
              {user ? (
                <>
                  <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-1 sm:gap-5 sm:space-y-0">
                    <Link
                      to="/ordering/restaurants"
                      className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                    >
                      Ordering user
                    </Link>
                  </div>
                  <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-1 sm:gap-5 sm:space-y-0">
                    <Link
                      to="/v2/ordering/restaurants"
                      className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                    >
                      Ordering user V2
                    </Link>
                  </div>
                  <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-1 sm:gap-5 sm:space-y-0">
                    {/* TODO hardcoded restaurant ID */}
                    <Link
                      to="/manager/restaurants/5ee006d0-e9d5-4ce7-a431-6cb0653296f9"
                      className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                    >
                      Restaurant manager
                    </Link>
                  </div>
                  <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-1 sm:gap-5 sm:space-y-0">
                    <Link
                      to="/v2/restaurants/orders"
                      className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                    >
                      Restaurant manager V2
                    </Link>
                  </div>
                  <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-1 sm:gap-5 sm:space-y-0">
                    <Link
                      to="/courier/delivery"
                      className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                    >
                      Delivery courier
                    </Link>
                  </div>
                  <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-1 sm:gap-5 sm:space-y-0">
                    <Link
                      to="/v2/courier/delivery"
                      className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                    >
                      Delivery courier V2
                    </Link>
                  </div>
                  <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-1 sm:gap-5 sm:space-y-0">
                    <Link
                      to="/auth/logout"
                      className="flex items-center justify-center rounded-md border border-transparent bg-white px-4 py-3 text-base font-medium text-yellow-700 shadow-sm hover:bg-yellow-50 sm:px-8"
                    >
                      LOGOUT
                    </Link>
                  </div>
                </>
              ) : (
                <div className="space-y-4 sm:mx-auto sm:inline-grid sm:grid-cols-2 sm:gap-5 sm:space-y-0">
                  <Form
                    action="/auth/auth0?screen_hint=signup"
                    method="post"
                    className="flex items-center justify-center rounded-md bg-gray-100 px-4 py-3 font-medium text-yellow-700 hover:bg-yellow-50"
                  >
                    <button>Sign up</button>
                  </Form>
                  <Form
                    action="/auth/auth0"
                    method="post"
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
