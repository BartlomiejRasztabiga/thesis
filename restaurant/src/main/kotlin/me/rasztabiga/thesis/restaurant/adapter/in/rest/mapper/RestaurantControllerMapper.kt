@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantRequest
import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.DeleteRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantCommand
import java.util.*

object RestaurantControllerMapper {
    fun mapToCreateRestaurantCommand(request: CreateRestaurantRequest): CreateRestaurantCommand {
        return CreateRestaurantCommand(
            id = request.id,
            name = request.name
        )
    }

    fun mapToUpdateRestaurantCommand(request: UpdateRestaurantRequest, restaurantId: UUID): UpdateRestaurantCommand {
        return UpdateRestaurantCommand(
            id = restaurantId,
            name = request.name
        )
    }

    fun mapToDeleteRestaurantCommand(restaurantId: UUID): DeleteRestaurantCommand {
        return DeleteRestaurantCommand(
            id = restaurantId
        )
    }
}
