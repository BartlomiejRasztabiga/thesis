package me.rasztabiga.thesis.delivery.domain.command.event

import org.axonframework.serialization.Revision

@Revision("1.0")
data class CourierCreatedEvent(
    val id: String,
    val name: String
)
