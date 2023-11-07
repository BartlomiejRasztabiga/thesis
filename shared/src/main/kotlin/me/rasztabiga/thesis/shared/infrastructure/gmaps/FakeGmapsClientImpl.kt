package me.rasztabiga.thesis.shared.infrastructure.gmaps

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.springframework.context.annotation.Profile
import kotlin.random.Random

@Profile("!gmaps")
class FakeGmapsClientImpl : GmapsClient {

    @Suppress("MagicNumber")
    override fun getDistanceInMeters(from: String, to: String): Long {
        val a = from.length
        val b = to.length
        return (a + b) * 100L
    }

    @Suppress("MagicNumber")
    override fun geocode(address: String): Location {
        val x = Random.nextDouble() / 10
        return Location(
            lat = 52.2370 + x,
            lng = 21.0175 + x,
            streetAddress = address
        )
    }
}
