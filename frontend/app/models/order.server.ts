import { getAxios } from "~/services/axios.server";
import type { UuidWrapper } from "~/models/utils.server";

export const getOrder = async (
  request: Request,
  orderId: string,
): Promise<OrderResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/orders/${orderId}`).then((res) => res.data);
};

export const startOrder = async (
  request: Request,
  restaurantId: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios.post(`/api/v1/orders`, { restaurantId }).then((res) => res.data);
};

interface OrderResponse {
  id: string;
  status: string;
}
