import type { LoaderArgs } from "@remix-run/node";
import { ActionArgs, json } from "@remix-run/node";
import { getRestaurants } from "~/models/restaurant.server";
import BottomNavbar from "~/components/BottomNavbar";
import Topbar from "~/components/Topbar";
import { Form, useLoaderData } from "@remix-run/react";
import { getCurrentUser, updateDefaultAddress } from "~/models/user.server";
import FormControl from "@mui/material/FormControl";
import { Button, MenuItem, TextField } from "@mui/material";

export async function loader({ request }: LoaderArgs) {
  const restaurants = await getRestaurants(request);
  const currentUser = await getCurrentUser(request);
  return json({ restaurants, currentUser });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  await updateDefaultAddress(request, values.deliveryAddressId);

  return json({});
}

export default function V2RestaurantsPage() {
  const data = useLoaderData<typeof loader>();

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div className="h-full">
        <div className="flex flex-col w-80 mx-auto">
          <Form method="post" className="mt-8">
            <FormControl>
              <TextField
                id="select-delivery-address"
                name="deliveryAddressId"
                select
                label="Please select your delivery address"
                defaultValue={data.currentUser.defaultAddressId}
              >
                {data.currentUser.deliveryAddresses.map((address) => (
                  <MenuItem key={address.id} value={address.id}>
                    {address.location.streetAddress}
                  </MenuItem>
                ))}
              </TextField>
              <Button variant="contained" type="submit">
                Save
              </Button>
            </FormControl>
          </Form>
        </div>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
