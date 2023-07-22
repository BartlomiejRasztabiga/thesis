@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateDeliveryAddressRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.order.domain.command.command.CreateDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.command.command.CreateUserCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteDeliveryAddressCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange
import java.util.*

object UserControllerMapper {
    fun mapToCreateUserCommand(request: CreateUserRequest, exchange: ServerWebExchange): CreateUserCommand {
        return CreateUserCommand(
            id = exchange.getUserId(),
            name = request.name
        )
    }

    fun mapToCreateDeliveryAddressCommand(
        request: CreateDeliveryAddressRequest,
        userId: String
    ): CreateDeliveryAddressCommand {
        return CreateDeliveryAddressCommand(
            userId = userId,
            addressId = UUID.randomUUID(),
            address = request.address,
            additionalInfo = request.additionalInfo
        )
    }

    fun mapToDeleteDeliveryAddressCommand(
        userId: String,
        addressId: UUID
    ): DeleteDeliveryAddressCommand {
        return DeleteDeliveryAddressCommand(
            userId = userId,
            addressId = addressId
        )
    }
}
