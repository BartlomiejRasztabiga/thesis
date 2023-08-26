import { getAxios } from "~/services/axios.server";

export const getCurrentCourier = async (
  request: Request
): Promise<CourierResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/couriers/me`).then((res) => res.data);
};

export const getCurrentDelivery = async (
  request: Request
): Promise<DeliveryResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/deliveries/current`).then((res) => res.data);
};

export const getDeliveryOffer = async (
  request: Request,
  courierLocation: string
): Promise<DeliveryResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/deliveries/offer?courierAddress=${courierLocation}`).then((res) => res.data);
};

export const acceptDeliveryOffer = async (
  request: Request,
  deliveryId: string
): Promise<void> => {
  const axios = await getAxios(request);
  return axios.put(`/api/v1/deliveries/${deliveryId}/accept`).then((res) => res.data);
};

export const rejectDeliveryOffer = async (
  request: Request,
  deliveryId: string
): Promise<void> => {
  const axios = await getAxios(request);
  return axios.put(`/api/v1/deliveries/${deliveryId}/reject`).then((res) => res.data);
};

export const pickupDelivery = async (
  request: Request,
  deliveryId: string
): Promise<void> => {
  const axios = await getAxios(request);
  return axios.put(`/api/v1/deliveries/${deliveryId}/pickup`).then((res) => res.data);
};

export const deliverDelivery = async (
  request: Request,
  deliveryId: string
): Promise<void> => {
  const axios = await getAxios(request);
  return axios.put(`/api/v1/deliveries/${deliveryId}/deliver`).then((res) => res.data);
};


export interface CourierResponse {
  id: string;
  name: string;
  availability: string;
}

export interface DeliveryResponse {
  id: string;
  restaurantAddress: string;
  distanceToRestaurantInKm?: number;
  deliveryAddress: string;
  distanceToDeliveryAddressInKm?: number;
  status: string;
  courierFee: number;
}
