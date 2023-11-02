import { getAxios } from "~/services/axios.server";

export const getCurrentPayee = async (
  request: Request,
): Promise<PayeeResponse> => {
  const axios = await getAxios(request);
  return axios.get(`/api/v2/payees/me`).then((res) => res.data);
};

export const withdrawBalance = async (
  request: Request,
  payeeId: string,
  amount: number,
  accountNumber: string,
): Promise<void> => {
  const axios = await getAxios(request);
  return axios
    .put(`/api/v1/payees/${payeeId}/withdraw`, {
      amount,
      targetBankAccount: accountNumber,
    })
    .then((res) => res.data);
};

export interface PayeeResponse {
  id: string;
  userId: string;
  name: string;
  email: string;
  balance: number;
}
