package me.rasztabiga.thesis.order.domain.command.event

import org.axonframework.serialization.Revision

@Revision("1.0")
data class UserCreatedEvent(
    val id: String,
    val name: String
)
