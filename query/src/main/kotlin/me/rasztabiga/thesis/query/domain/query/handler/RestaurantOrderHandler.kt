package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantOrderEntity
import me.rasztabiga.thesis.query.domain.query.exception.RestaurantOrderNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.RestaurantOrderMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.RestaurantOrderMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.query.FindAllRestaurantOrdersByRestaurantQuery
import me.rasztabiga.thesis.query.domain.query.repository.RestaurantOrderRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantOrderResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryPickedUpEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderPreparedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderRejectedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@Component
@ProcessingGroup("projection")
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

    @EventHandler
    fun on(event: OrderDeliveryPickedUpEvent) {
        val entity = getOrderByOrderId(event.orderId)
        entity.status = RestaurantOrderEntity.OrderStatus.PICKED_UP
        restaurantOrderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryDeliveredEvent) {
        val entity = getOrderByOrderId(event.orderId)
        entity.status = RestaurantOrderEntity.OrderStatus.DELIVERED
        restaurantOrderRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindAllRestaurantOrdersByRestaurantQuery): Flux<RestaurantOrderResponse> {
        return restaurantOrderRepository.loadAllByRestaurantId(query.restaurantId).map { mapToResponse(it) }
    }

    private fun getOrder(id: UUID): RestaurantOrderEntity {
        return restaurantOrderRepository.load(id) ?: throw RestaurantOrderNotFoundException(id)
    }

    private fun getOrderByOrderId(orderId: UUID): RestaurantOrderEntity {
        return restaurantOrderRepository.loadByOrderId(orderId) ?: throw RestaurantOrderNotFoundException(orderId)
    }
}
