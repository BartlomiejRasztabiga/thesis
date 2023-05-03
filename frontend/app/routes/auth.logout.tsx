import type { ActionArgs } from "@remix-run/node";

import { redirect } from "@remix-run/node";

// import { destroySession, getSession } from "~/services/auth.server";
//
// export const action = async ({ request }: ActionArgs) => {
//     const session = await getSession(request.headers.get("Cookie"));
//     const logoutURL = new URL(process.env.AUTH0_LOGOUT_URL); // i.e https://YOUR_TENANT.us.auth0.com/v2/logout
//
//     logoutURL.searchParams.set("client_id", process.env.AUTH0_CLIENT_ID);
//     logoutURL.searchParams.set("returnTo", process.env.AUTH0_RETURN_TO_URL);
//
//     return redirect(logoutURL.toString(), {
//         headers: {
//             "Set-Cookie": await destroySession(session),
//         },
//     });
// };
