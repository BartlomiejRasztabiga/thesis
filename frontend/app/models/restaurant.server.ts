import { getAxios } from "~/services/axios.server";

export const getRestaurants = async (
  request: Request,
): Promise<RestaurantResponse[]> => {
  const axios = await getAxios(request);
  return axios.get("/api/v1/restaurants").then((res) => res.data);
};

export const getRestaurant = async (
  request: Request,
  restaurantId: string,
): Promise<RestaurantResponse> => {
  const axios = await getAxios(request);
  return axios
    .get(`/api/v1/restaurants/${restaurantId}`)
    .then((res) => res.data);
};

export const getRestaurantOrders = async (
  request: Request,
  restaurantId: string,
): Promise<RestaurantOrderResponse[]> => {
  const axios = await getAxios(request);
  return axios
    .get(`/api/v1/restaurants/${restaurantId}/orders`)
    .then((res) => res.data);
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
  CLOSED = "CLOSED",
}

interface RestaurantOrderResponse {
  restaurantOrderId: string;
  items: RestaurantOrderItemResponse[];
  status: RestaurantOrderStatus;
}

interface RestaurantOrderItemResponse {
  productId: string;
}

enum RestaurantOrderStatus {
  NEW = "NEW",
  ACCEPTED = "ACCEPTED",
  PREPARED = "PREPARED",
  REJECTED = "REJECTED",
}
