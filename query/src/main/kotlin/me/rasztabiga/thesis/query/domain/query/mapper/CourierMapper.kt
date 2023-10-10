package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent

object CourierMapper {

    fun mapToEntity(event: CourierCreatedEvent): CourierEntity {
        return CourierEntity(
            id = event.courierId,
            name = event.name,
            email = event.email,
            availability = CourierEntity.Availability.OFFLINE,
            location = null
        )
    }

    fun mapToResponse(entity: CourierEntity): CourierResponse {
        return CourierResponse(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            availability = CourierResponse.Availability.valueOf(entity.availability.name),
            location = entity.location
        )
    }
}
