package me.rasztabiga.thesis.delivery.domain.query.port

interface DistanceCalculatorPort {
    fun calculateDistance(from: String, to: String): Double
}
