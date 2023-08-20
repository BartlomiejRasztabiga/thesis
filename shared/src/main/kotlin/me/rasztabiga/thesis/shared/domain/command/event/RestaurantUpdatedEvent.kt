package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class RestaurantUpdatedEvent(
    val id: UUID,
    val name: String
)
