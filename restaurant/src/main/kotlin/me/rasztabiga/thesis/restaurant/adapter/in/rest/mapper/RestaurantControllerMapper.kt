@file:Suppress("InvalidPackageDeclaration")
package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.CreateRestaurantRequest

object RestaurantControllerMapper {
    fun mapToCreateRestaurantCommand(request: CreateRestaurantRequest): CreateRestaurantCommand {
        return CreateRestaurantCommand(
            id = request.id,
            name = request.name
        )
    }
}
