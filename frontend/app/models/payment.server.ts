import { getAxios } from "~/services/axios.server";

export const getCurrentPayee = async (
  request: Request,
): Promise<PayeeResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v1/payees/me`).then((res) => res.data);
};

export interface PayeeResponse {
  id: string;
  userId: string;
  name: string;
  email: string;
  balance: number;
}
