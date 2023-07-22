import { LoaderArgs } from "@remix-run/node";
import invariant from "tiny-invariant";
import { authenticator } from "~/services/auth.server";

export let loader = async ({ request }: LoaderArgs) => {
  invariant(process.env.AUTH_CLIENT_ID, "AUTH_CLIENT_ID must be set");
  invariant(process.env.AUTH_CALLBACK_URL, "AUTH_CALLBACK_URL must be set");

  const logoutURL = new URL("https://rasztabigab.eu.auth0.com/v2/logout");
  const returnToURL = process.env.AUTH_CALLBACK_URL.slice(
    0,
    -"/auth/auth0/callback".length,
  );

  logoutURL.searchParams.set("client_id", process.env.AUTH_CLIENT_ID);
  logoutURL.searchParams.set("returnTo", returnToURL);

  return authenticator.logout(request, {
    redirectTo: logoutURL.toString(),
  });
};
