import { getAxios } from "~/services/axios.server";
import type { UuidWrapper } from "~/models/utils.server";

export const getCurrentUser = async (
  request: Request,
): Promise<UserResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v2/users/me`).then((res) => res.data);
};

export const createUser = async (
  request: Request,
  name: string,
  email: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios
    .post(`/api/v1/users`, { name, email })
    .then((res) => res.data);
}

export const createDeliveryAddress = async (
  request: Request,
  userId: string,
  address: string,
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .post(`/api/v1/users/${userId}/addresses`, { address, additionalInfo: null })
    .then((res) => res.data);
}

export const updateDefaultAddress = async (
  request: Request,
  addressId: string,
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .put(`/api/v1/users/me/default-address`, { addressId })
    .then((res) => res.data);
};

export interface UserResponse {
  id: string;
  name: string;
  deliveryAddresses: DeliveryAddressResponse[];
  defaultAddressId?: string;
}

export interface DeliveryAddressResponse {
  id: string;
  location: Location;
}

export interface Location {
  lat: number;
  lng: number;
  streetAddress?: string;
}
