package me.rasztabiga.thesis.restaurant.domain.query.handler

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantOrderResponse
import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantOrderCreatedEvent
import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantOrderEntity
import me.rasztabiga.thesis.restaurant.domain.query.exception.RestaurantOrderNotFoundException
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantOrderMapper.mapToEntity
import me.rasztabiga.thesis.restaurant.domain.query.mapper.RestaurantOrderMapper.mapToResponse
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantOrdersByRestaurantQuery
import me.rasztabiga.thesis.restaurant.domain.query.repository.RestaurantOrderRepository
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderPreparedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderRejectedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

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

    @EventHandler
    fun on(event: RestaurantOrderAcceptedEvent) {
        val entity = getOrder(event.restaurantOrderId)
        entity.status = RestaurantOrderEntity.OrderStatus.ACCEPTED
        restaurantOrderRepository.save(entity)
    }

    @EventHandler
    fun on(event: RestaurantOrderPreparedEvent) {
        val entity = getOrder(event.restaurantOrderId)
        entity.status = RestaurantOrderEntity.OrderStatus.PREPARED
        restaurantOrderRepository.save(entity)
    }

    @EventHandler
    fun on(event: RestaurantOrderRejectedEvent) {
        val entity = getOrder(event.restaurantOrderId)
        entity.status = RestaurantOrderEntity.OrderStatus.REJECTED
        restaurantOrderRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindAllRestaurantOrdersByRestaurantQuery): Flux<RestaurantOrderResponse> {
        return restaurantOrderRepository.loadAllByRestaurantId(query.restaurantId).map { mapToResponse(it) }
    }

    private fun getOrder(id: UUID): RestaurantOrderEntity {
        return restaurantOrderRepository.load(id) ?: throw RestaurantOrderNotFoundException(id)
    }
}
