package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse

object RestaurantMapper {

    fun mapToEntity(event: RestaurantCreatedEvent): RestaurantEntity {
        return RestaurantEntity(
            id = event.id,
            name = event.name,
            availability = RestaurantEntity.Availability.CLOSED,
            menu = emptyList(),
            address = event.address
        )
    }

    fun mapToResponse(entity: RestaurantEntity): RestaurantResponse {
        return RestaurantResponse(
            id = entity.id,
            name = entity.name,
            availability = RestaurantResponse.Availability.valueOf(entity.availability.name),
            menu = entity.menu.map {
                RestaurantResponse.Product(it.id, it.name, it.description, it.price)
            },
            address = entity.address
        )
    }
}
