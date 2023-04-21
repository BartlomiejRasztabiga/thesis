@file:Suppress("InvalidPackageDeclaration")
package me.rasztabiga.thesis.restaurant.domain.query.handler

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.adapter.out.db.RestaurantRepository
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantMapper.mapToEntity
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantMapper.mapToResponse
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
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

    @Suppress("UnusedParameter")
    @QueryHandler
    fun on(query: FindAllRestaurantsQuery): List<RestaurantResponse> {
        return restaurantRepository.findAll().map { mapToResponse(it) }
    }
}
