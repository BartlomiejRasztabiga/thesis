import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import { Form, useActionData } from "@remix-run/react";
import { Button, FormControl, Paper, TextField } from "@mui/material";
import { getEmail } from "~/services/session.server";
import { createDeliveryAddress, createUser } from "~/models/user.server";
import { toast } from "react-toastify";

export async function loader({ request, params }: LoaderArgs) {
  return json({});
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  try {
    const email = await getEmail(request);
    const userId = await createUser(request, values.name, email!);
    await createDeliveryAddress(request, userId.id, values.address);
  } catch (e) {
    console.log(e);
    return json({ error: e.response.data.message });
  }

  await new Promise((resolve) => setTimeout(resolve, 1000));

  return redirect("/v2/ordering/restaurants");
}

export default function V2SetupUserPage() {
  const actionData = useActionData();

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1 });
  }

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto items-center">
          <h1 className="text-xl">Create new user</h1>
          <Form method="post">
            <FormControl>
              <TextField name="name" label="Name" variant="outlined" />
              <TextField name="address" label="Address" variant="outlined" />
              <Button variant="contained" type="submit">
                Setup user
              </Button>
            </FormControl>
          </Form>
        </Paper>
      </div>
    </div>
  );
}
