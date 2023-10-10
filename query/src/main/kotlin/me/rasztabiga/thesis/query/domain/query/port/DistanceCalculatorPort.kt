package me.rasztabiga.thesis.query.domain.query.port

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location

interface DistanceCalculatorPort {
    fun calculateDistance(from: Location, to: Location): Double
}
