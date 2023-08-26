package me.rasztabiga.thesis.delivery.adapter.out.gmaps

import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import me.rasztabiga.thesis.delivery.domain.command.port.CalculateDeliveryFeePort
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@Profile("gmaps")
class GmapsCalculateDeliveryFeeAdapter(
    @Value("\${gmaps.api.key}")
    private val gmapsApiKey: String
) : CalculateDeliveryFeePort {

    @Suppress("MagicNumber")
    override fun calculateBaseFee(restaurantAddress: String, deliveryAddress: String): BigDecimal {
        val context = GeoApiContext.Builder().apiKey(gmapsApiKey).build()
        val request = DistanceMatrixApi.newRequest(context)
            .origins(restaurantAddress)
            .destinations(deliveryAddress)
            .mode(TravelMode.DRIVING)

        val response = request.await()
        val distanceInMeters = response.rows[0].elements[0].distance.inMeters

        val feePerKm = 2.2

        return BigDecimal.valueOf(distanceInMeters * feePerKm / 1000)
    }
}
