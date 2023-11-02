import { getAxios } from "~/services/axios.server";

export const getCurrentUser = async (
  request: Request,
): Promise<UserResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v2/users/me`).then((res) => res.data);
};

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
