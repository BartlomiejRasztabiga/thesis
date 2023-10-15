package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import java.math.BigDecimal

object RestaurantMapper {

    fun mapToEntity(event: RestaurantCreatedEvent): RestaurantEntity {
        return RestaurantEntity(
            id = event.restaurantId,
            managerId = event.managerId,
            name = event.name,
            email = event.email,
            availability = RestaurantEntity.Availability.CLOSED,
            menu = emptyList(),
            location = event.location,
            imageUrl = event.imageUrl
        )
    }

    fun mapToResponse(entity: RestaurantEntity, deliveryFee: BigDecimal?): RestaurantResponse {
        return RestaurantResponse(
            id = entity.id,
            managerId = entity.managerId,
            name = entity.name,
            email = entity.email,
            availability = RestaurantResponse.Availability.valueOf(entity.availability.name),
            menu = entity.menu.map {
                RestaurantResponse.Product(it.id, it.name, it.description, it.price, it.imageUrl)
            },
            location = entity.location,
            imageUrl = entity.imageUrl,
            avgRating = 5.0, // TODO
            deliveryFee = deliveryFee
        )
    }
}
