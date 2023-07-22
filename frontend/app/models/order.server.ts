import invariant from "tiny-invariant";
import { getAxios } from "~/services/axios.server";
import { UuidWrapper } from "~/models/utils.server";

export const getOrder = async (
  request: Request,
  orderId: string,
): Promise<OrderResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/orders/${orderId}`).then((res) => res.data);
};

export const startOrder = async (
  request: Request,
  userId: string,
  restaurantId: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios
    .post(`/api/v1/orders`, { userId, restaurantId })
    .then((res) => res.data);
};

interface OrderResponse {
  id: string;
  status: string;
}
