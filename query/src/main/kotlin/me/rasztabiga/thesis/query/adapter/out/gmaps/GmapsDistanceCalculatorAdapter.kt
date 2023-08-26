package me.rasztabiga.thesis.query.adapter.out.gmaps

import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("gmaps")
class GmapsDistanceCalculatorAdapter(
    @Value("\${gmaps.api.key}")
    private val gmapsApiKey: String
) : DistanceCalculatorPort {

    override fun calculateDistance(from: String, to: String): Double {
        val context = GeoApiContext.Builder().apiKey(gmapsApiKey).build()
    }
}
