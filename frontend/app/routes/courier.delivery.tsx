import type { ActionArgs, LoaderArgs } from "@remix-run/node";
import { json } from "@remix-run/node";
import { Form, Link, useActionData, useFetcher, useLoaderData, useRevalidator } from "@remix-run/react";
import React, { useEffect } from "react";
import {
  acceptDeliveryOffer,
  deliverDelivery,
  getCurrentCourier,
  getCurrentDelivery,
  getDeliveryOffer,
  pickupDelivery,
  rejectDeliveryOffer,
  updateCourierLocation
} from "~/models/delivery.server";
import invariant from "tiny-invariant";
import { toast } from "react-toastify";

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

  return json({ courier, currentDelivery, deliveryOffer });
}

export async function action({ request, params }: ActionArgs) {
  const formData = await request.formData();
  const { _action, ...values } = Object.fromEntries(formData);

  try {
    if (_action === "updateLocation") {
      await updateCourierLocation(request, { lat: values.lat, lng: values.lng });
      return json({});
    }

    const deliveryId = values.deliveryId as string;
    invariant(deliveryId, "deliveryId not found");

    if (_action === "accept") {
      await acceptDeliveryOffer(request, deliveryId);
    }

    if (_action === "reject") {
      await rejectDeliveryOffer(request, deliveryId);
    }

    if (_action === "pickup") {
      await pickupDelivery(request, deliveryId);
    }

    if (_action === "deliver") {
      await deliverDelivery(request, deliveryId);
    }
  } catch (e) {
    return json({ error: e.response.data.message });
  }

  return json({});
}

export default function CourierDeliveryPage() {
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
            { method: "POST" }
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
  }, [revalidator, data.currentDelivery]);

  const getGmapsLink = (address: string) => {
    return `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(
      address
    )}`;
  };

  const getContent = () => {
    const className =
      "rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 focus:bg-blue-400 mx-2";

    if (data.currentDelivery) {
      return (
        <>
          <p>Delivery in progress</p>
          {/* TODO buttons to navigate */}
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
                <button
                  type="submit"
                  className={className}
                  name="_action"
                  value="pickup"
                >
                  Pickup
                </button>
                <button className={className}>
                  <a
                    href={getGmapsLink(
                      data.currentDelivery.restaurantLocation.streetAddress
                    )}
                    target={"_blank"}
                    rel="noreferrer"
                  >
                    Navigate to restaurant
                  </a>
                </button>
              </>
            ) : (
              <>
                <button
                  type="submit"
                  className={className}
                  name="_action"
                  value="deliver"
                >
                  Deliver
                </button>
                <button className={className}>
                  <a
                    href={getGmapsLink(
                      data.currentDelivery.deliveryLocation.streetAddress
                    )}
                    target={"_blank"}
                    rel="noreferrer"
                  >
                    Navigate to delivery address
                  </a>
                </button>
              </>
            )}
          </Form>
        </>
      );
    }

    if (data.deliveryOffer) {
      return (
        <>
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
          <Form method="post">
            <input
              type={"hidden"}
              name={"deliveryId"}
              value={data.deliveryOffer.id}
            />
            <button
              type="submit"
              className={className}
              name="_action"
              value="accept"
            >
              Accept
            </button>
            <button
              type="submit"
              className={className}
              name="_action"
              value="reject"
            >
              Reject
            </button>
          </Form>
        </>
      );
    }

    return (
      <>
        <p>No offers available, wait a bit</p>
      </>
    );
  };

  if (actionData && actionData.error) {
    toast.error(actionData.error, { toastId: 1 });
  }

  return (
    <div className="flex h-full min-h-screen flex-col">
      <header className="flex items-center justify-between bg-slate-800 p-4 text-white">
        <div className="flex items-start text-2xl mr-4 font-bold">
          <h1>{data.courier.name}</h1>
        </div>
        <button>
          <Link to="/auth/logout">Logout</Link>
        </button>
      </header>

      <main className="flex h-full bg-white">
        <div className="flex-1 p-6">
          <p className="py-6">{data.courier.availability}</p>
          {/* TODO update availability */}
          <hr className="my-4" />
          <div>
            {/* TODO content */}
            {getContent()}
          </div>
        </div>
      </main>
    </div>
  );
}
