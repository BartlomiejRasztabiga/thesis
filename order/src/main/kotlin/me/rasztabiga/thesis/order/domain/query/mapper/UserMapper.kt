package me.rasztabiga.thesis.order.domain.query.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.order.domain.command.event.UserCreatedEvent
import me.rasztabiga.thesis.order.domain.query.entity.UserEntity

object UserMapper {

    fun mapToEntity(event: UserCreatedEvent): UserEntity {
        return UserEntity(
            event.id,
            event.name
        )
    }

    fun mapToResponse(entity: UserEntity): UserResponse {
        return UserResponse(
            entity.id,
            entity.name
        )
    }
}
