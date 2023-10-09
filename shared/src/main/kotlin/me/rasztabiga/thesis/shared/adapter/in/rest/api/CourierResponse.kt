package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

data class CourierResponse(
    val id: String,
    val name: String,
    val email: String,
    val availability: Availability,
    val location: Location?
) {
    enum class Availability {
        ONLINE, OFFLINE
    }
}
