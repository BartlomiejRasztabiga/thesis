import type { Axios } from "axios";
import axios from "axios";
import { redirect } from "@remix-run/node";

import invariant from "tiny-invariant";
import { getAccessToken } from "~/services/session.server";

invariant(process.env.API_GATEWAY_URL, "API_GATEWAY_URL must be set");

export const getAxios = async (request: Request): Promise<Axios> => {
  const accessToken = await getAccessToken(request);
  if (!accessToken) {
    throw new Response("Unauthorized", { status: 401 });
  }

  const axiosInstance = axios.create({
    baseURL: process.env.API_GATEWAY_URL,
    headers: {
      Authorization: `Bearer ${accessToken}`
    }
  });

  axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response.status === 401) {
        return redirect("/");
      }
      return Promise.reject(error);
    }
  );

  return axiosInstance
};
