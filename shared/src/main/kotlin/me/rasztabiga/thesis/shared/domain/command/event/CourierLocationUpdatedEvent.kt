package me.rasztabiga.thesis.shared.domain.command.event

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.axonframework.serialization.Revision

@Revision("1.0")
data class CourierLocationUpdatedEvent(
    val id: String,
    val location: Location
)
