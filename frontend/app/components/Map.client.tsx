import type { LatLngTuple } from "leaflet";
import { FeatureGroup, MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import type { Location } from "~/models/user.server";

export interface MapProps {
  height: string;
  restaurantLocation: Location;
  deliveryLocation: Location;
  courierLocation?: Location;
}

export function Map(props: MapProps) {
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
    courierLatLng = [
      props.courierLocation.lat,
      props.courierLocation.lng
    ];

    bounds.push(courierLatLng);
  }

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
          <Marker position={restaurantLatLng}>
            <Popup>Restaurant location</Popup>
          </Marker>
          <Marker position={deliveryLatLng}>
            <Popup>Delivery location</Popup>
          </Marker>
          {props.courierLocation &&
            (<Marker position={courierLatLng!}>
              <Popup>Courier location</Popup>
            </Marker>)
          }
        </FeatureGroup>
      </MapContainer>
    </div>
  );
}
