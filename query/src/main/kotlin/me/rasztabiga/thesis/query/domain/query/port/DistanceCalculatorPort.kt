package me.rasztabiga.thesis.query.domain.query.port

interface DistanceCalculatorPort {
    fun calculateDistance(from: String, to: String): Double
}
