package me.rasztabiga.thesis.restaurant.domain.query.handler

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantOrderResponse
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantOrderCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantOrderMapper.mapToEntity
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantOrderMapper.mapToResponse
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantOrdersByRestaurantQuery
import me.rasztabiga.thesis.restaurant.domain.query.repository.RestaurantOrderRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
@ProcessingGroup("restaurantorder")
class RestaurantOrderHandler(
    private val restaurantOrderRepository: RestaurantOrderRepository
) {

    @EventHandler
    fun on(event: RestaurantOrderCreatedEvent) {
        val entity = mapToEntity(event)
        restaurantOrderRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindAllRestaurantOrdersByRestaurantQuery): Flux<RestaurantOrderResponse> {
        return restaurantOrderRepository.loadAllByRestaurantId(query.restaurantId).map { mapToResponse(it) }
    }
}
