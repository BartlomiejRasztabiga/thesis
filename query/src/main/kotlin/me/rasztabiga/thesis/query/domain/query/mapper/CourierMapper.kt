package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.adapter.`in`.rest.api.Availability
import me.rasztabiga.thesis.query.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent

object CourierMapper {

    fun mapToEntity(event: CourierCreatedEvent): CourierEntity {
        return CourierEntity(
            id = event.id,
            name = event.name,
            availability = CourierEntity.Availability.OFFLINE
        )
    }

    fun mapToResponse(entity: CourierEntity): CourierResponse {
        return CourierResponse(
            id = entity.id,
            name = entity.name,
            availability = Availability.valueOf(entity.availability.name)
        )
    }
}
