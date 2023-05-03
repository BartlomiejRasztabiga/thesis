import { Authenticator } from "remix-auth";
import { Auth0Strategy } from "remix-auth-auth0";

import {sessionStorage} from "~/services/session.server";
import invariant from "tiny-invariant";

invariant(process.env.AUTH_CALLBACK_URL, "AUTH_CALLBACK_URL must be set");
invariant(process.env.AUTH_CLIENT_ID, "AUTH_CLIENT_ID must be set");
invariant(process.env.AUTH_CLIENT_SECRET, "AUTH_CLIENT_SECRET must be set");

// Create an instance of the authenticator, pass a generic with what your
// strategies will return and will be stored in the session
export const authenticator = new Authenticator<any>(sessionStorage);

let auth0Strategy = new Auth0Strategy(
    {
        callbackURL: process.env.AUTH_CALLBACK_URL,
        clientID: process.env.AUTH_CLIENT_ID,
        clientSecret: process.env.AUTH_CLIENT_SECRET,
        domain: "rasztabigab.eu.auth0.com",
        scope: "openid profile email read:restaurants write:restaurants read:users write:users",
        audience: "https://thesis.rasztabiga.me/api"
    },
    async ({ accessToken, refreshToken, extraParams, profile }) => {
        return {
            ...profile,
            ...extraParams
        }
    }
);

authenticator.use(auth0Strategy);
