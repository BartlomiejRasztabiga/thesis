import { createCookieSessionStorage, redirect } from "@remix-run/node";
import invariant from "tiny-invariant";

invariant(process.env.SESSION_SECRET, "SESSION_SECRET must be set");

export const sessionStorage = createCookieSessionStorage({
  cookie: {
    name: "__session",
    httpOnly: true,
    path: "/",
    sameSite: "lax",
    secrets: [process.env.SESSION_SECRET],
    secure: process.env.NODE_ENV === "production",
  },
});

export async function getSession(request: Request) {
  const cookie = request.headers.get("Cookie");
  return sessionStorage.getSession(cookie);
}

export async function destroySession(request: Request) {
  const session = await getSession(request);
  return sessionStorage.destroySession(session);
}

export async function getAccessToken(
  request: Request,
): Promise<string | undefined> {
  const session = await getSession(request);
  if (!session.data.user) return undefined;
  return session.data.user.accessToken;
}

export async function getUserId(request: Request): Promise<string | undefined> {
  const session = await getSession(request);
  if (!session.data.user) return undefined;
  return session.data.user.id;
}

export async function getUser(request: Request) {
  const userId = await getUserId(request);
  if (userId === undefined) return null;

  return {
    id: userId,
  };
}

export async function requireUserId(
  request: Request,
  redirectTo: string = new URL(request.url).pathname,
) {
  const userId = await getUserId(request);
  if (!userId) {
    const searchParams = new URLSearchParams([["redirectTo", redirectTo]]);
    throw redirect(`/login?${searchParams}`);
  }
  return userId;
}

export async function setOrderId(request: Request, orderId: string) {
  const session = await getSession(request);
  session.set("orderId", orderId);
  return sessionStorage.commitSession(session);
}

export async function getOrderId(request: Request) {
  const session = await getSession(request);
  return session.get("orderId");
}

export async function clearOrderId(request: Request) {
  const session = await getSession(request);
  session.unset("orderId");
  return sessionStorage.commitSession(session);
}
