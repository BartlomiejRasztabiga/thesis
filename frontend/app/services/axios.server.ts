import axios, {Axios} from 'axios';

import invariant from "tiny-invariant";

invariant(process.env.API_GATEWAY_URL, "API_GATEWAY_URL must be set");

export const getAxios = (accessToken: string): Axios => {
    return axios.create({
        baseURL: process.env.API_GATEWAY_URL,
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    })
}
