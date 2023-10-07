package me.rasztabiga.thesis.payment.domain.command.port

interface DistanceCalculatorPort {
    fun calculateDistance(from: String, to: String): Double
}
