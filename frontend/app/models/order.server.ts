import { getAxios } from "~/services/axios.server";
import type { UuidWrapper } from "~/models/utils.server";
import type { Location } from "~/models/user.server";

export const getOrder = async (
  request: Request,
  orderId: string,
): Promise<OrderResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/orders/${orderId}`).then((res) => res.data);
};

export const getOrders = async (request: Request): Promise<OrderResponse[]> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/orders`).then((res) => res.data);
};

export const startOrder = async (
  request: Request,
  restaurantId: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios.post(`/api/v1/orders`, { restaurantId }).then((res) => res.data);
};

export const cancelOrder = async (
  request: Request,
  orderId: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios.delete(`/api/v1/orders/${orderId}`).then((res) => res.data);
};

export const finalizeOrder = async (
  request: Request,
  orderId: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios
    .put(`/api/v1/orders/${orderId}/finalize`)
    .then((res) => res.data);
};

export const addOrderItem = async (
  request: Request,
  orderId: string,
  productId: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios
    .post(`/api/v1/orders/${orderId}/items`, { productId })
    .then((res) => res.data);
};

export const deleteOrderItem = async (
  request: Request,
  orderId: string,
  productId: string,
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .delete(`/api/v1/orders/${orderId}/items`, { data: { productId } })
    .then((res) => res.data);
};

export const rateOrder = async (
  request: Request,
  orderId: string,
  rating: number,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios
    .post(`/api/v1/orders/${orderId}/rating`, { rating })
    .then((res) => res.data);
};

export interface OrderResponse {
  id: string;
  restaurantId: string;
  restaurantLocation: Location;
  deliveryLocation: Location;
  courierLocation: Location;
  total: number;
  deliveryFee: number;
  userId: string;
  status: OrderStatus;
  items: OrderItem[];
  itemsTotal: number;
  paymentId: string;
  paymentSessionUrl: string;
  createdAt: string;
  events: OrderEvent[];
}

type OrderItem = {
  [id: string]: number;
};

interface OrderEvent {
  type: OrderEventType;
  createdAt: string;
}

enum OrderEventType {
  CONFIRMED = "CONFIRMED",
  COURIER_ASSIGNED = "COURIER_ASSIGNED",
  PREPARED = "PREPARED",
  PICKED_UP = "PICKED_UP",
  DELIVERED = "DELIVERED",
}

enum OrderStatus {
  CREATED = "CREATED",
  CANCELED = "CANCELED",
  FINALIZED = "FINALIZED",
  PAID = "PAID",
  CONFIRMED = "CONFIRMED",
  REJECTED = "REJECTED",
  PREPARED = "PREPARED",
  PICKED_UP = "PICKED_UP",
  DELIVERED = "DELIVERED",
}
