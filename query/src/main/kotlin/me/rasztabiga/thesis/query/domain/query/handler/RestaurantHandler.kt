package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.query.domain.query.exception.RestaurantNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.RestaurantMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.RestaurantMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.query.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.query.domain.query.repository.RestaurantRepository
import me.rasztabiga.thesis.query.domain.query.repository.UserRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantMenuUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantUpdatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
@ProcessingGroup("restaurant")
class RestaurantHandler(
    private val restaurantRepository: RestaurantRepository,
    private val userRepository: UserRepository,
    private val distanceCalculatorPort: CalculateDeliveryFeePort
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
        val user = userRepository.load(query.userId)

        return restaurantRepository.loadAll().map {
            val deliveryLocation =
                user?.deliveryAddresses?.find { address -> address.id == user.defaultAddressId }?.location
            val deliveryFee =
                deliveryLocation?.let { location -> distanceCalculatorPort.calculateDeliveryFee(it.location, location) }

            mapToResponse(it, deliveryFee)
        }
    }

    @QueryHandler
    fun handle(query: FindRestaurantByIdQuery): Mono<RestaurantResponse> {
        val restaurant =
            restaurantRepository.load(query.restaurantId) ?: throw RestaurantNotFoundException(query.restaurantId)

        val user = query.userId?.let { userRepository.load(it) }
        val deliveryLocation =
            user?.deliveryAddresses?.find { address -> address.id == user.defaultAddressId }?.location
        val deliveryFee =
            deliveryLocation?.let { location ->
                distanceCalculatorPort.calculateDeliveryFee(
                    restaurant.location,
                    location
                )
            }

        return Mono.just(mapToResponse(restaurant, deliveryFee))
    }
}
