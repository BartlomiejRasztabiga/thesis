import { Authenticator } from "remix-auth";
import { Auth0Strategy } from "remix-auth-auth0";

import {sessionStorage} from "~/services/session.server";

interface User {
    email: string
}

// Create an instance of the authenticator, pass a generic with what your
// strategies will return and will be stored in the session
export const authenticator = new Authenticator<any>(sessionStorage);

let auth0Strategy = new Auth0Strategy(
    {
        callbackURL: "http://localhost:3000/auth/auth0/callback",
        clientID: "3gSK8og83MMTCUlNrzGNfnzpBNyLlpTS",
        clientSecret: "fxntskIoYgCCXgxT-DYzTZraK4OwX-mwAcv8C4kZdszLDedqdtuwgTK8xB840C2G",
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
