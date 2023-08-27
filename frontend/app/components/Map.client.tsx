import type { LatLngTuple } from "leaflet";
import { FeatureGroup, MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import { useEffect, useState } from "react";

export function Map({ height }: { height: string }) {
  const [currentLocation, setCurrentLocation] = useState<LatLngTuple>([52.2370, 21.0175]);

  useEffect(() => {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        setCurrentLocation([position.coords.latitude, position.coords.longitude]);
      });
    } else {
      console.log("Geolocation not available");
    }
  }, []);

  // TODO set restaurant location and delivery location
  const bounds = [[52.2370, 21.0175], [52.2370, 21.0175]];

  return (
    <div style={{ height }}>
      <MapContainer
        style={{
          height: "100%"
        }}
        center={currentLocation}
        zoom={13}
        scrollWheelZoom={true}
        trackResize={true}
        bounds={bounds}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <FeatureGroup>
          <Marker position={currentLocation}>
            {/* TODO current location */}
            {/* TODO courier location */}
            {/* TODO restaurant location */}
            <Popup>
              Delivery location
            </Popup>
          </Marker>
        </FeatureGroup>
      </MapContainer>
    </div>
  );
}
