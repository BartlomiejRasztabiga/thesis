package me.rasztabiga.thesis.shared.infrastructure.gmaps

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location

interface GmapsClient {

    fun getDistanceInMeters(from: String, to: String): Long

    fun geocode(address: String): Location
}
