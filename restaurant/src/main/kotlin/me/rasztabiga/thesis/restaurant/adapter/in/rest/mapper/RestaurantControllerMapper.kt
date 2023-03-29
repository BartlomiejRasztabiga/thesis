package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.api.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.api.rest.CreateRestaurantRequest

object RestaurantControllerMapper {
    fun mapToCreateRestaurantCommand(request: CreateRestaurantRequest): CreateRestaurantCommand {
        return CreateRestaurantCommand(
            restaurantId = request.restaurantId,
            name = request.name
        )
    }
}
