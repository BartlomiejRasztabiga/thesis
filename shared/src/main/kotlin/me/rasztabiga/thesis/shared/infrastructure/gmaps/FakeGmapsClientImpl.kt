package me.rasztabiga.thesis.shared.infrastructure.gmaps

import org.springframework.context.annotation.Profile

@Profile("!gmaps")
class FakeGmapsClientImpl : GmapsClient {

    @Suppress("MagicNumber")
    override fun getDistanceInMeters(from: String, to: String): Long {
        val a = from.length
        val b = to.length
        return (a + b).toLong() * 100
    }
}
