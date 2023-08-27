package me.rasztabiga.thesis.shared.infrastructure.gmaps

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.context.annotation.Profile

@Profile("!gmaps")
class FakeGmapsClientImpl : GmapsClient {

    @Suppress("MagicNumber")
    override fun getDistanceInMeters(from: String, to: String): Long {
        val a = from.length
        val b = to.length
        return (a + b).toLong() * 100
    }

    override fun geocode(address: String): Location {
        return Location(
            lat = 52.2370,
            lng = 21.0175,
            streetAddress = address
        )
    }
}
