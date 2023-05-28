import invariant from "tiny-invariant";
import {getAxios} from "~/services/axios.server";

invariant(process.env.API_GATEWAY_URL, "API_GATEWAY_URL must be set");

export const getRestaurants = (accessToken: string): Promise<RestaurantResponse[]> => {
    // TODO tzeba to za kazdym razem przekazywac z gory?
    const axios = getAxios(accessToken);
    return axios.get('/api/v1/restaurants').then((res) => res.data);
}

interface RestaurantResponse {
    id: string;
    name: string;
    availability: Availability;
    menu: MenuEntryResponse[];
}

interface MenuEntryResponse {
    id: string;
    name: string;
    description: string;
    price: number;
}

enum Availability {
    OPEN = "OPEN",
    CLOSED = "CLOSED"
}
