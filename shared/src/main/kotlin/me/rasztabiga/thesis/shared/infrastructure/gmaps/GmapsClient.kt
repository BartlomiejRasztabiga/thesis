package me.rasztabiga.thesis.shared.infrastructure.gmaps

interface GmapsClient {

    fun getDistanceInMeters(from: String, to: String): Long
}
