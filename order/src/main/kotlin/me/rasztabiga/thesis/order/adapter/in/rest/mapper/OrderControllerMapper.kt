@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.config.getUserId
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
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
}
