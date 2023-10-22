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

export const getCurrentRestaurant = async (
  request: Request,
): Promise<RestaurantResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/restaurants/me`).then((res) => res.data);
};

export const getRestaurantOrders = async (
  request: Request,
  restaurantId: string,
): Promise<RestaurantOrderResponse[]> => {
  const axios = await getAxios(request);
  return axios
    .get(`/api/v1/restaurants/${restaurantId}/orders`)
    .then((res) => res.data);
};

export const acceptRestaurantOrder = async (
  request: Request,
  restaurantId: string,
  restaurantOrderId: string,
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .put(
      `/api/v1/restaurants/${restaurantId}/orders/${restaurantOrderId}/accept`,
    )
    .then((res) => res.data);
};

export const rejectRestaurantOrder = async (
  request: Request,
  restaurantId: string,
  restaurantOrderId: string,
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .put(
      `/api/v1/restaurants/${restaurantId}/orders/${restaurantOrderId}/reject`,
    )
    .then((res) => res.data);
};

export const prepareRestaurantOrder = async (
  request: Request,
  restaurantId: string,
  restaurantOrderId: string,
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .put(
      `/api/v1/restaurants/${restaurantId}/orders/${restaurantOrderId}/prepare`,
    )
    .then((res) => res.data);
};

export const updateRestaurantAvailability = async (
  request: Request,
  restaurantId: string,
  availability: string
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .put(`/api/v1/restaurants/${restaurantId}/availability`, { availability: availability })
    .then((res) => res.data);
}

export interface RestaurantResponse {
  id: string;
  name: string;
  deliveryFee: number;
  imageUrl: string;
  availability: Availability;
  menu: MenuEntryResponse[];
  avgRating: number;
}

interface MenuEntryResponse {
  id: string;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
}

enum Availability {
  OPEN = "OPEN",
  CLOSED = "CLOSED",
}

export interface RestaurantOrderResponse {
  restaurantOrderId: string;
  items: RestaurantOrderItemResponse[];
  status: RestaurantOrderStatus;
  createdAt: string;
}

interface RestaurantOrderItemResponse {
  productId: string;
}

export enum RestaurantOrderStatus {
  NEW = "NEW",
  ACCEPTED = "ACCEPTED",
  PREPARED = "PREPARED",
  REJECTED = "REJECTED",
}
