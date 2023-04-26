@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.domain.query.handler

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantUpdatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantMapper.mapToEntity
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantMapper.mapToResponse
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

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

    @EventHandler
    fun on(event: RestaurantUpdatedEvent) {
        val entity = restaurantRepository.load(event.id) ?: error("Restaurant not found: ${event.id}")
        val updatedEntity = entity.copy(name = event.name)
        restaurantRepository.save(updatedEntity)
    }

    @Suppress("UnusedParameter")
    @QueryHandler
    fun on(query: FindAllRestaurantsQuery): Flux<RestaurantResponse> {
        return restaurantRepository.loadAll().map { mapToResponse(it) }
    }
}
