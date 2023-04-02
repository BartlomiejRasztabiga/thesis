package me.rasztabiga.thesis.restaurant.domain.query.mapper

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity

object RestaurantMapper {

    fun mapToEntity(event: RestaurantCreatedEvent): RestaurantEntity {
        return RestaurantEntity(event.id, event.name)
    }

    fun mapToResponse(entity: RestaurantEntity): RestaurantResponse {
        return RestaurantResponse(entity.id, entity.name)
    }
}
