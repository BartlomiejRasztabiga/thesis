import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json, redirect } from "@remix-run/node";
import { Form, useActionData } from "@remix-run/react";
import { Button, FormControl, Paper, TextField } from "@mui/material";
import { getEmail } from "~/services/session.server";
import { createRestaurant } from "~/models/restaurant.server";
import { toast } from "react-toastify";

export async function loader({ request, params }: LoaderArgs) {
  return json({});
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  try {
    const email = await getEmail(request);
    await createRestaurant(request, values.name, values.address, email!, values.imageUrl);
    console.log("test");
  } catch (e) {
    console.log(e);
    return json({ error: e.response.data.message });
  }

  await new Promise(resolve => setTimeout(resolve, 1000));

  return redirect("/v2/restaurants/orders");
}

export default function V2SetupRestaurantPage() {
  const actionData = useActionData();

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1 });
  }

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto items-center">
          <h1 className="text-xl">Create new restaurant</h1>
          <Form method="post">
            <FormControl>
              <TextField
                name="name"
                label="Name"
                variant="outlined"
              />
              <TextField
                name="address"
                label="Address"
                variant="outlined"
              />
              <TextField
                name="imageUrl"
                label="Image url"
                variant="outlined"
              />
              <Button variant="contained" type="submit">
                Setup restaurant
              </Button>
            </FormControl>
          </Form>
        </Paper>
      </div>
    </div>
  );
}
