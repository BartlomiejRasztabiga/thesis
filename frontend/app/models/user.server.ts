import { getAxios } from "~/services/axios.server";

export const getCurrentUser = async (
  request: Request
): Promise<UserResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/users/me`).then((res) => res.data);
};

export interface UserResponse {
  id: string;
  name: string;
  deliveryAddresses: DeliveryAddressResponse[];
}

export interface DeliveryAddressResponse {
  id: string;
  address: string;
  additionalInfo: string;
}
