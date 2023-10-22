import type { LoaderArgs, ActionArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import BottomNavbar from "~/components/courier/BottomNavbar";
import {
  Form,
  useActionData,
  useFetcher,
  useLoaderData,
  useRevalidator,
} from "@remix-run/react";
import invariant from "tiny-invariant";
import { useEffect } from "react";
import { toast } from "react-toastify";
import Topbar from "~/components/courier/Topbar";
import { Button, Paper } from "@mui/material";
import {
  acceptDeliveryOffer,
  deliverDelivery,
  getCurrentCourier,
  getCurrentDelivery,
  getDeliveryOffer,
  pickupDelivery,
  rejectDeliveryOffer,
  updateCourierAvailability,
  updateCourierLocation,
} from "~/models/delivery.server";
import { getCurrentPayee } from "~/models/payment.server";

export async function loader({ request, params }: LoaderArgs) {
  const courier = await getCurrentCourier(request);

  let currentDelivery;

  try {
    currentDelivery = await getCurrentDelivery(request);
  } catch (error) {
    // ignore, no delivery in progress
  }

  let deliveryOffer;

  if (!currentDelivery) {
    try {
      deliveryOffer = await getDeliveryOffer(request);
    } catch (error) {
      // ignore, no delivery offer
    }
  }

  const payee = await getCurrentPayee(request);

  return json({ courier, currentDelivery, deliveryOffer, payee });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  try {
    if (_action === "updateLocation") {
      console.log("updateLocation");
      await updateCourierLocation(request, {
        lat: values.lat,
        lng: values.lng,
      });
      return json({});
    }

    if (_action === "update_availability") {
      const currentAvailability = values.availability as string;
      const newAvailability =
        currentAvailability === "ONLINE" ? "OFFLINE" : "ONLINE";

      await updateCourierAvailability(request, newAvailability);
    }

    const deliveryId = values.deliveryId as string;

    if (_action === "accept") {
      invariant(deliveryId, "deliveryId not found");
      await acceptDeliveryOffer(request, deliveryId);
    }

    if (_action === "reject") {
      invariant(deliveryId, "deliveryId not found");
      await rejectDeliveryOffer(request, deliveryId);
    }

    if (_action === "pickup") {
      invariant(deliveryId, "deliveryId not found");
      await pickupDelivery(request, deliveryId);
    }

    if (_action === "deliver") {
      invariant(deliveryId, "deliveryId not found");
      await deliverDelivery(request, deliveryId);
    }
  } catch (e) {
    return json({ error: e.response.data.message });
  }

  return json({});
}

export default function V2CourierPage() {
  const data = useLoaderData<typeof loader>();
  const actionData = useActionData();

  const revalidator = useRevalidator();

  const fetcher = useFetcher();

  // TODO good enough for now ???
  useEffect(() => {
    const timer = setInterval(async () => {
      // TODO jak przeslac te dane na serwer?
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(async (position) => {
          const { latitude, longitude } = position.coords;

          fetcher.submit(
            { _action: "updateLocation", lat: latitude, lng: longitude },
            { method: "POST" },
          );
        });
      }

      // TODO only if no delivery in progress
      // disabled for now, we want to update courier's location
      // if (!data.currentDelivery) {
      revalidator.revalidate();
      // }
    }, 5000);
    return () => {
      clearInterval(timer);
    };
  }, [revalidator, data.currentDelivery, fetcher]);

  const getGmapsLink = (address: string) => {
    return `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(
      address,
    )}`;
  };

  // TODO update to MUI
  const getContent = () => {
    if (data.currentDelivery) {
      return (
        <div className="justify-center">
          <p>Delivery in progress</p>
          {/* TODO Buttons to navigate */}
          <p>From: {data.currentDelivery.restaurantLocation.streetAddress}</p>
          <p>To: {data.currentDelivery.deliveryLocation.streetAddress}</p>
          <p>Status: {data.currentDelivery.status}</p>
          <p>Reward: {data.currentDelivery.courierFee} PLN</p>
          <Form method="post">
            <input
              type={"hidden"}
              name={"deliveryId"}
              value={data.currentDelivery.id}
            />
            {data.currentDelivery.status === "ACCEPTED" ? (
              <>
                <Button
                  type="submit"
                  name="_action"
                  value="pickup"
                  variant="contained"
                >
                  Pickup
                </Button>
                <Button variant="contained">
                  <a
                    href={getGmapsLink(
                      data.currentDelivery.restaurantLocation.streetAddress,
                    )}
                    target={"_blank"}
                    rel="noreferrer"
                  >
                    Navigate to restaurant
                  </a>
                </Button>
              </>
            ) : (
              <>
                <Button
                  type="submit"
                  name="_action"
                  value="deliver"
                  variant="contained"
                >
                  Deliver
                </Button>
                <Button variant="contained">
                  <a
                    href={getGmapsLink(
                      data.currentDelivery.deliveryLocation.streetAddress,
                    )}
                    target={"_blank"}
                    rel="noreferrer"
                  >
                    Navigate to delivery address
                  </a>
                </Button>
              </>
            )}
          </Form>
        </div>
      );
    }

    if (data.deliveryOffer) {
      return (
        <div className="justify-center">
          <p>Delivery offer</p>
          <p>
            From: {data.deliveryOffer.restaurantLocation.streetAddress} (
            {data.deliveryOffer.distanceToRestaurantInKm.toFixed(2)} km)
          </p>
          <p>
            To: {data.deliveryOffer.deliveryLocation.streetAddress} (
            {data.deliveryOffer.distanceToDeliveryAddressInKm.toFixed(2)} km)
          </p>
          <p>Reward: {data.deliveryOffer.courierFee} PLN</p>
          <Form method="post" className="flex justify-between">
            <input
              type={"hidden"}
              name={"deliveryId"}
              value={data.deliveryOffer.id}
            />
            <Button
              type="submit"
              name="_action"
              value="accept"
              variant="contained"
            >
              Accept
            </Button>
            <Button
              type="submit"
              name="_action"
              value="reject"
              variant="contained"
            >
              Reject
            </Button>
          </Form>
        </div>
      );
    }

    return (
      <div className="flex justify-center">
        <p>No offers available, wait for a bit</p>
      </div>
    );
  };

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1, position: "bottom-center" });
  }

  return (
    <div className="flex flex-col h-full overflow-x-hidden">
      <div>
        <Topbar payee={data.payee} courier={data.courier} />
      </div>
      <div className="h-full">
        <Paper className="flex flex-col w-80 mx-auto">{getContent()}</Paper>
      </div>
      <div>
        <BottomNavbar />
      </div>
    </div>
  );
}
