package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.UserEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent

object UserMapper {

    fun mapToEntity(event: UserCreatedEvent): UserEntity {
        return UserEntity(
            id = event.userId,
            name = event.name,
            email = event.email,
            deliveryAddresses = mutableListOf(),
            defaultAddressId = null
        )
    }

    fun mapToResponse(entity: UserEntity): UserResponse {
        return UserResponse(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            deliveryAddresses = entity.deliveryAddresses.map {
                UserResponse.DeliveryAddress(
                    it.id,
                    it.location
                )
            },
            defaultAddressId = entity.defaultAddressId
        )
    }
}
