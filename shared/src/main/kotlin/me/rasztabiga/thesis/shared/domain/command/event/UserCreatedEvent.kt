package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision

@Revision("1.0")
data class UserCreatedEvent(
    val userId: String,
    val name: String
)
