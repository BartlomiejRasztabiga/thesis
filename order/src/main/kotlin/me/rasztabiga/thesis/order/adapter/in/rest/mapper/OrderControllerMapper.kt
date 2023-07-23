@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.AddOrderItemRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.domain.command.command.AddOrderItemCommand
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
}
