@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.payment.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.payment.domain.command.command.AcceptRestaurantOrderCommand
import me.rasztabiga.thesis.payment.domain.command.command.PrepareRestaurantOrderCommand
import me.rasztabiga.thesis.payment.domain.command.command.RejectRestaurantOrderCommand
import java.util.*

object RestaurantOrderControllerMapper {
    fun mapToAcceptOrderCommand(
        restaurantId: UUID,
        orderId: UUID
    ): AcceptRestaurantOrderCommand {
        return AcceptRestaurantOrderCommand(
            restaurantId = restaurantId,
            restaurantOrderId = orderId
        )
    }

    fun mapToRejectOrderCommand(
        restaurantId: UUID,
        orderId: UUID
    ): RejectRestaurantOrderCommand {
        return RejectRestaurantOrderCommand(
            restaurantId = restaurantId,
            restaurantOrderId = orderId
        )
    }

    fun mapToPrepareOrderCommand(
        restaurantId: UUID,
        orderId: UUID
    ): PrepareRestaurantOrderCommand {
        return PrepareRestaurantOrderCommand(
            restaurantId = restaurantId,
            restaurantOrderId = orderId
        )
    }
}
