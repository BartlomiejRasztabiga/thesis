import type { LatLngTuple } from "leaflet";
import { FeatureGroup, MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import { useEffect, useState } from "react";
import { Location } from "~/models/user.server";

export interface MapProps {
  height: string;
  restaurantLocation: Location;
  deliveryLocation: Location;
}

export function Map(props: MapProps) {
  // const [center, setCenter] = useState<LatLngTuple>([52.2370, 21.0175]);

  const restaurantLatLng: LatLngTuple = [props.restaurantLocation.lat, props.restaurantLocation.lng];
  const deliveryLatLng: LatLngTuple = [props.deliveryLocation.lat, props.deliveryLocation.lng];

  useEffect(() => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        // setCurrentLocation([position.coords.latitude, position.coords.longitude]);
      });
    } else {
      console.log("Geolocation not available");
    }
  }, []);

  // TODO set restaurant location and delivery location
  const bounds = [restaurantLatLng, deliveryLatLng];

  console.log(props)

  return (
    <div style={{ height: props.height }}>
      <MapContainer
        style={{
          height: "100%"
        }}
        center={deliveryLatLng}
        zoom={14}
        scrollWheelZoom={true}
        trackResize={true}
        bounds={bounds}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <FeatureGroup>
          {/* TODO courier location */}

          <Marker position={restaurantLatLng}>
            <Popup>
              Restaurant location
            </Popup>
          </Marker>
          <Marker position={deliveryLatLng}>
            <Popup>
              Delivery location
            </Popup>
          </Marker>
        </FeatureGroup>
      </MapContainer>
    </div>
  );
}
