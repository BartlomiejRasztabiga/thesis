package me.rasztabiga.thesis.restaurant.domain.query.handler

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantAvailabilityUpdatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantMenuUpdatedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantUpdatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.restaurant.domain.query.exception.RestaurantNotFoundException
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantMapper.mapToEntity
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantMapper.mapToResponse
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.restaurant.domain.query.query.FindRestaurantByIdQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
        val entity = restaurantRepository.load(event.id) ?: throw RestaurantNotFoundException(event.id)
        val updatedEntity = entity.copy(name = event.name)
        restaurantRepository.save(updatedEntity)
    }

    @EventHandler
    fun on(event: RestaurantDeletedEvent) {
        restaurantRepository.delete(event.id)
    }

    @EventHandler
    fun on(event: RestaurantAvailabilityUpdatedEvent) {
        val entity = restaurantRepository.load(event.id) ?: throw RestaurantNotFoundException(event.id)
        val updatedEntity = entity.copy(availability = RestaurantEntity.Availability.valueOf(event.availability.name))
        restaurantRepository.save(updatedEntity)
    }

    @EventHandler
    fun on(event: RestaurantMenuUpdatedEvent) {
        val entity = restaurantRepository.load(event.id) ?: throw RestaurantNotFoundException(event.id)
        val updatedEntity = entity.copy(menu = event.menu.map {
            RestaurantEntity.Product(it.id, it.name, it.description, it.price)
        })
        restaurantRepository.save(updatedEntity)
    }

    @Suppress("UnusedParameter")
    @QueryHandler
    fun handle(query: FindAllRestaurantsQuery): Flux<RestaurantResponse> {
        return restaurantRepository.loadAll().map { mapToResponse(it) }
    }

    @QueryHandler
    fun handle(query: FindRestaurantByIdQuery): Mono<RestaurantResponse> {
        return restaurantRepository.load(query.restaurantId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(RestaurantNotFoundException(query.restaurantId))
    }
}
