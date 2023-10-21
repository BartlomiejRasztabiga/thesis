import type { LatLngTuple } from "leaflet";
import {
  FeatureGroup,
  MapContainer,
  Marker,
  Popup,
  TileLayer,
} from "react-leaflet";
import type { Location } from "~/models/user.server";
import { Home, List, Settings, Logout } from "@mui/icons-material";
import { divIcon } from "leaflet";
import { renderToStaticMarkup } from "react-dom/server";
import {
  Badge,
  Fab,
  IconButton,
  Typography,
  Paper,
  CardContent,
  Card,
  CardMedia,
} from "@mui/material";

export interface MapProps {
  height: string;
  restaurantLocation: Location;
  deliveryLocation: Location;
  courierLocation?: Location;
}

export function MapClient(props: MapProps) {
  const restaurantLatLng: LatLngTuple = [
    props.restaurantLocation.lat,
    props.restaurantLocation.lng,
  ];
  const deliveryLatLng: LatLngTuple = [
    props.deliveryLocation.lat,
    props.deliveryLocation.lng,
  ];

  let bounds = [restaurantLatLng, deliveryLatLng];

  let courierLatLng: LatLngTuple | undefined;

  if (props.courierLocation) {
    courierLatLng = [props.courierLocation.lat, props.courierLocation.lng];

    bounds.push(courierLatLng);
  }

  const homeIcon = divIcon({
    html: renderToStaticMarkup(
      <IconButton size={"large"}>
        <Home fontSize="larger" />
      </IconButton>,
    ),
  });

  return (
    <div style={{ height: props.height }}>
      <MapContainer
        style={{
          height: "100%",
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
          <Marker position={restaurantLatLng}>
            <Popup>Restaurant location</Popup>
          </Marker>
          <Marker position={deliveryLatLng}>
            <Popup>Delivery location</Popup>
          </Marker>
          {props.courierLocation && (
            <Marker position={courierLatLng!}>
              <Popup>Courier location</Popup>
            </Marker>
          )}
        </FeatureGroup>
      </MapContainer>
    </div>
  );
}
