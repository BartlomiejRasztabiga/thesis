package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision

@Revision("1.0")
data class CourierAvailabilityUpdatedEvent(
    val id: String,
    val availability: Availability
) {
    enum class Availability {
        ONLINE, OFFLINE
    }
}
