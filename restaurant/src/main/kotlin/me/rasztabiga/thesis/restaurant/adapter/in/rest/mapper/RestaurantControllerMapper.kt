@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantAvailabilityRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantMenuRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantRequest
import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Availability
import me.rasztabiga.thesis.restaurant.domain.command.aggregate.Product
import me.rasztabiga.thesis.restaurant.domain.command.command.CreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.DeleteRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantAvailabilityCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.command.command.UpdateRestaurantMenuCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange
import java.util.*

object RestaurantControllerMapper {
    fun mapToCreateRestaurantCommand(
        request: CreateRestaurantRequest,
        exchange: ServerWebExchange
    ): CreateRestaurantCommand {
        return CreateRestaurantCommand(
            id = request.id,
            managerId = exchange.getUserId(),
            email = request.email,
            name = request.name,
            address = request.address
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

    fun mapToUpdateRestaurantAvailabilityCommand(
        request: UpdateRestaurantAvailabilityRequest,
        restaurantId: UUID
    ): UpdateRestaurantAvailabilityCommand {
        return UpdateRestaurantAvailabilityCommand(
            id = restaurantId,
            availability = Availability.valueOf(request.availability.name)
        )
    }

    fun mapToUpdateRestaurantMenuCommand(
        request: UpdateRestaurantMenuRequest,
        restaurantId: UUID
    ): UpdateRestaurantMenuCommand {
        return UpdateRestaurantMenuCommand(
            id = restaurantId,
            menu = request.menu.map {
                Product(
                    id = UUID.randomUUID(),
                    name = it.name,
                    description = it.description,
                    price = it.price
                )
            }
        )
    }
}
