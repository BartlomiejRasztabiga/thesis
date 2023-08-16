import { getAxios } from "~/services/axios.server";
import type { UuidWrapper } from "~/models/utils.server";

export const payPayment = async (
  request: Request,
  paymentId: string,
): Promise<UuidWrapper> => {
  const axios = await getAxios(request);
  return axios.put(`/api/v1/payments/${paymentId}/pay`).then((res) => res.data);
};
