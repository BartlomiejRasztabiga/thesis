package me.rasztabiga.thesis.restaurant.domain.query.mapper

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.Availability
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity

object RestaurantMapper {

    fun mapToEntity(event: RestaurantCreatedEvent): RestaurantEntity {
        return RestaurantEntity(
            event.id,
            event.name,
            RestaurantEntity.Availability.CLOSED,
            emptyList()
        )
    }

    fun mapToResponse(entity: RestaurantEntity): RestaurantResponse {
        return RestaurantResponse(
            entity.id,
            entity.name,
            RestaurantResponse.Availability.valueOf(entity.availability.name),
            entity.menu.map {
                RestaurantResponse.Product(it.id, it.name, it.description, it.price)
            }
        )
    }
}
