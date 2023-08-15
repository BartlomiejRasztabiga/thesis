package me.rasztabiga.thesis.delivery.domain.command.event

import me.rasztabiga.thesis.delivery.domain.command.aggregate.Availability
import org.axonframework.serialization.Revision

@Revision("1.0")
data class CourierAvailabilityUpdatedEvent(
    val id: String,
    val availability: Availability
)
