package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.query.domain.query.exception.OrderNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.OrderMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.OrderMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.repository.OrderRepository
import me.rasztabiga.thesis.query.domain.query.repository.RestaurantRepository
import me.rasztabiga.thesis.query.domain.query.repository.UserRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryPickedUpEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderItemDeletedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderRejectedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderStartedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderTotalCalculatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderPreparedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Suppress("TooManyFunctions")
@Component
@ProcessingGroup("order")
class OrderHandler(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository
) {

    @EventHandler
    fun on(event: OrderStartedEvent) {
        val restaurant = restaurantRepository.load(event.restaurantId)

        val restaurantLocation = restaurant?.location ?: error("Restaurant not found")

        val entity = mapToEntity(event, restaurantLocation)
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderCanceledEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.CANCELED
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderItemAddedEvent) {
        val entity = getOrder(event.orderId)
        entity.items.add(OrderEntity.OrderItem(event.orderItemId, event.productId))
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderItemDeletedEvent) {
        val entity = getOrder(event.orderId)
        entity.items.removeIf { it.id == event.orderItemId }
        orderRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindOrderByIdQuery): Mono<OrderResponse> {
        return orderRepository.load(query.orderId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(OrderNotFoundException(query.orderId))
    }

    @EventHandler
    fun on(event: OrderFinalizedEvent) {
        val entity = getOrder(event.orderId)
        val user = userRepository.load(entity.userId) ?: error("User not found")

        val deliveryLocation = user.deliveryAddresses.find { it.id == entity.deliveryAddressId }?.location

        entity.status = OrderEntity.OrderStatus.FINALIZED
        entity.deliveryAddressId = event.deliveryAddressId
        entity.deliveryLocation = deliveryLocation

        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderTotalCalculatedEvent) {
        val entity = getOrder(event.orderId)
        entity.total = event.total
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderPaymentCreatedEvent) {
        val entity = getOrder(event.orderId)
        entity.paymentId = event.id
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderPaidEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.PAID
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: RestaurantOrderAcceptedEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.CONFIRMED
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: RestaurantOrderPreparedEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.PREPARED
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderRejectedEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.REJECTED
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryAcceptedEvent) {
        val entity = getOrder(event.orderId)
        entity.courierId = event.courierId
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryPickedUpEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.PICKED_UP
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryDeliveredEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.DELIVERED
        orderRepository.save(entity)
    }

    private fun getOrder(orderId: UUID): OrderEntity {
        return orderRepository.load(orderId) ?: throw OrderNotFoundException(orderId)
    }
}
