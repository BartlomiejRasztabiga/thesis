import invariant from "tiny-invariant";
import {getAxios} from "~/services/axios.server";

invariant(process.env.API_GATEWAY_URL, "API_GATEWAY_URL must be set");

export const getRestaurants = async (request: Request): Promise<RestaurantResponse[]> => {
    const axios = await getAxios(request);
    return axios.get('/api/v1/restaurants').then((res) => res.data);
}

export const getRestaurant = async (request: Request, restaurantId: string): Promise<RestaurantResponse> => {
    const axios = await getAxios(request);
    return axios.get(`/api/v1/restaurants/${restaurantId}`).then((res) => res.data);
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
