@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.domain.command.command.AcceptRestaurantOrderCommand
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
}
