package me.rasztabiga.thesis.order.domain.query.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.order.domain.command.event.UserCreatedEvent
import me.rasztabiga.thesis.order.domain.query.entity.UserEntity

object UserMapper {

    fun mapToEntity(event: UserCreatedEvent): UserEntity {
        return UserEntity(
            id = event.id,
            name = event.name,
            deliveryAddresses = mutableListOf()
        )
    }

    fun mapToResponse(entity: UserEntity): UserResponse {
        return UserResponse(
            id = entity.id,
            name = entity.name,
            deliveryAddresses = entity.deliveryAddresses.map {
                UserResponse.DeliveryAddress(
                    it.id,
                    it.address,
                    it.additionalInfo
                )
            }
        )
    }
}
