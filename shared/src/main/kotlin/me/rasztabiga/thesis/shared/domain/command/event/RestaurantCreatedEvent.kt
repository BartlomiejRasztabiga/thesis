package me.rasztabiga.thesis.shared.domain.command.event

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class RestaurantCreatedEvent(
    val id: UUID,
    val name: String,
    val location: Location,
    val managerId: String
)
