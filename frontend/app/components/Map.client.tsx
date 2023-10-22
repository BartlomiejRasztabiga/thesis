import type { LatLngTuple } from "leaflet";
import { divIcon } from "leaflet";
import { FeatureGroup, MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import type { Location } from "~/models/user.server";
import { Home, Restaurant, DeliveryDining  } from "@mui/icons-material";
import { renderToStaticMarkup } from "react-dom/server";

export interface MapProps {
  height: string;
  restaurantLocation: Location;
  deliveryLocation: Location;
  courierLocation?: Location;
}

export function MapClient(props: MapProps) {
  const restaurantLatLng: LatLngTuple = [
    props.restaurantLocation.lat,
    props.restaurantLocation.lng
  ];
  const deliveryLatLng: LatLngTuple = [
    props.deliveryLocation.lat,
    props.deliveryLocation.lng
  ];

  let bounds = [restaurantLatLng, deliveryLatLng];

  let courierLatLng: LatLngTuple | undefined;

  if (props.courierLocation) {
    courierLatLng = [props.courierLocation.lat, props.courierLocation.lng];
  }

  const restaurantIcon = divIcon({
    html: renderToStaticMarkup(
      <Restaurant />
    ),
    iconSize: [30, 30]
  });

  const deliveryIcon = divIcon({
    html: renderToStaticMarkup(
      <Home />
    ),
    iconSize: [30, 30]
  });

  const courierIcon = divIcon({
    html: renderToStaticMarkup(
      <DeliveryDining />
    ),
    iconSize: [30, 30]
  });

  return (
    <div style={{ height: props.height }}>
      <MapContainer
        style={{
          height: "100%"
        }}
        center={deliveryLatLng}
        scrollWheelZoom={true}
        trackResize={true}
        bounds={bounds}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <FeatureGroup>
          <Marker position={restaurantLatLng} icon={restaurantIcon}>
            <Popup>Restaurant location</Popup>
          </Marker>
          <Marker position={deliveryLatLng} icon={deliveryIcon}>
            <Popup>Delivery location</Popup>
          </Marker>
          {props.courierLocation && (
            <Marker position={courierLatLng!} icon={courierIcon}>
              <Popup>Courier location</Popup>
            </Marker>
          )}
        </FeatureGroup>
      </MapContainer>
    </div>
  );
}
