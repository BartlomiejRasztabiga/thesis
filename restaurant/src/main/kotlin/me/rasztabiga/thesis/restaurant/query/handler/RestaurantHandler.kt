package me.rasztabiga.thesis.restaurant.query.handler

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.domain.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.infrastructure.RestaurantRepository
import me.rasztabiga.thesis.restaurant.query.mapper.RestaurantMapper.mapToEntity
import me.rasztabiga.thesis.restaurant.query.mapper.RestaurantMapper.mapToResponse
import me.rasztabiga.thesis.restaurant.query.query.FindAllRestaurantsQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("restaurant")
class RestaurantHandler(
    private val restaurantRepository: RestaurantRepository
) {

    @EventHandler
    fun on(event: RestaurantCreatedEvent) {
        val entity = mapToEntity(event)
        restaurantRepository.save(entity)
    }

    @QueryHandler
    fun on(query: FindAllRestaurantsQuery): List<RestaurantResponse> {
        return restaurantRepository.findAll().map { mapToResponse(it) }
    }
}
