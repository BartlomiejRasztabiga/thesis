@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.StartOrderRequest
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import java.util.*

object OrderControllerMapper {
    fun mapToStartOrderCommand(request: StartOrderRequest): StartOrderCommand {
        return StartOrderCommand(
            orderId = UUID.randomUUID(),
            userId = request.userId,
            restaurantId = request.restaurantId
        )
    }
}
