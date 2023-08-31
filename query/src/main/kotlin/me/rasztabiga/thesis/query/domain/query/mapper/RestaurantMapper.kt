package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent

object RestaurantMapper {

    fun mapToEntity(event: RestaurantCreatedEvent): RestaurantEntity {
        return RestaurantEntity(
            id = event.restaurantId,
            managerId = event.managerId,
            name = event.name,
            availability = RestaurantEntity.Availability.CLOSED,
            menu = emptyList(),
            location = event.location
        )
    }

    fun mapToResponse(entity: RestaurantEntity): RestaurantResponse {
        return RestaurantResponse(
            id = entity.id,
            managerId = entity.managerId,
            name = entity.name,
            availability = RestaurantResponse.Availability.valueOf(entity.availability.name),
            menu = entity.menu.map {
                RestaurantResponse.Product(it.id, it.name, it.description, it.price)
            },
            location = entity.location
        )
    }
}
