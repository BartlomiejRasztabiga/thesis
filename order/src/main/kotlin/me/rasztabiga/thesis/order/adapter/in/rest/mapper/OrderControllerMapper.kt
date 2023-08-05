@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.AddOrderItemRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.FinalizeOrderRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.domain.command.command.AddOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.CancelOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.DeleteOrderItemCommand
import me.rasztabiga.thesis.order.domain.command.command.FinalizeOrderCommand
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange
import java.util.*

object OrderControllerMapper {
    fun mapToStartOrderCommand(
        request: StartOrderRequest,
        exchange: ServerWebExchange
    ): StartOrderCommand {
        return StartOrderCommand(
            orderId = UUID.randomUUID(),
            userId = exchange.getUserId(),
            restaurantId = request.restaurantId
        )
    }

    fun mapToCancelOrderCommand(
        orderId: UUID,
        exchange: ServerWebExchange
    ): CancelOrderCommand {
        return CancelOrderCommand(
            orderId = orderId,
            userId = exchange.getUserId()
        )
    }

    fun mapToAddOrderItemCommand(
        orderId: UUID,
        request: AddOrderItemRequest,
        exchange: ServerWebExchange
    ): AddOrderItemCommand {
        return AddOrderItemCommand(
            orderId = orderId,
            userId = exchange.getUserId(),
            orderItemId = UUID.randomUUID(),
            productId = request.productId
        )
    }

    fun mapToDeleteOrderItemCommand(
        orderId: UUID,
        orderItemId: UUID,
        exchange: ServerWebExchange
    ): DeleteOrderItemCommand {
        return DeleteOrderItemCommand(
            orderId = orderId,
            userId = exchange.getUserId(),
            orderItemId = orderItemId
        )
    }

    fun mapToFinalizeOrderCommand(
        orderId: UUID,
        request: FinalizeOrderRequest,
        exchange: ServerWebExchange
    ): FinalizeOrderCommand {
        return FinalizeOrderCommand(
            orderId = orderId,
            userId = exchange.getUserId(),
            deliveryAddressId = request.deliveryAddressId
        )
    }
}
