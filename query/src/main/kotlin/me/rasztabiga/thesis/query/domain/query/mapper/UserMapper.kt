package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.PayeeEntity
import me.rasztabiga.thesis.query.domain.query.entity.UserEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent

object UserMapper {

    fun mapToEntity(event: PayeeCreatedEvent): PayeeEntity {
        return PayeeEntity(
            id = event.userId,
            name = event.name,
            deliveryAddresses = mutableListOf()
        )
    }

    fun mapToResponse(entity: UserEntity): PayeeResponse {
        return PayeeResponse(
            id = entity.id,
            name = entity.name,
            deliveryAddresses = entity.deliveryAddresses.map {
                UserResponse.DeliveryAddress(
                    it.id,
                    it.location
                )
            }
        )
    }
}
