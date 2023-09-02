package me.rasztabiga.thesis.shared.adapter.`in`.rest.api

data class CourierResponse(
    val id: String,
    val name: String,
    val availability: Availability
) {
    enum class Availability {
        ONLINE, OFFLINE
    }
}
