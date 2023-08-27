package me.rasztabiga.thesis.shared.infrastructure.gmaps

import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.model.TravelMode
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile

@Profile("gmaps")
class GmapsClientImpl(
    @Value("\${gmaps.api.key}")
    private val gmapsApiKey: String
) : GmapsClient {

    override fun getDistanceInMeters(from: String, to: String): Long {
        val context = GeoApiContext.Builder().apiKey(gmapsApiKey).build()
        val request = DistanceMatrixApi.newRequest(context)
            .origins(from)
            .destinations(to)
            .mode(TravelMode.DRIVING)

        val response = request.await()
        return response.rows[0].elements[0].distance.inMeters
    }

    override fun geocode(address: String): Location {
        val context = GeoApiContext.Builder().apiKey(gmapsApiKey).build()
        val request = GeocodingApi.newRequest(context).address(address)

        val response = request.await()
        return Location(
            lat = response[0].geometry.location.lat,
            lng = response[0].geometry.location.lng,
            streetAddress = response[0].formattedAddress
        )
    }
}
