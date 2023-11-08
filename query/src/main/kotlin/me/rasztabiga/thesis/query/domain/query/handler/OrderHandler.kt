package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.query.domain.query.exception.OrderNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.OrderMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.OrderMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.repository.CourierRepository
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
import me.rasztabiga.thesis.shared.domain.query.query.FindOrdersByUserIdQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Suppress("TooManyFunctions")
@Component
@ProcessingGroup("projection")
class OrderHandler(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val restaurantRepository: RestaurantRepository,
    private val courierRepository: CourierRepository
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
        if (entity.items.containsKey(event.productId)) {
            entity.items[event.productId] = entity.items[event.productId]!! + 1
        } else {
            entity.items[event.productId] = 1
        }
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderItemDeletedEvent) {
        val entity = getOrder(event.orderId)
        if (entity.items[event.productId]!! > 1) {
            entity.items[event.productId] = entity.items[event.productId]!! - 1
        } else {
            entity.items.remove(event.productId)
        }
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderFinalizedEvent) {
        val entity = getOrder(event.orderId)
        val user = userRepository.load(entity.userId) ?: error("User not found")

        val deliveryLocation = user.deliveryAddresses.find { it.id == user.defaultAddressId }?.location

        entity.status = OrderEntity.OrderStatus.FINALIZED
        entity.deliveryAddressId = user.defaultAddressId
        entity.deliveryLocation = deliveryLocation

        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderTotalCalculatedEvent) {
        val entity = getOrder(event.orderId)
        entity.productsTotal = event.productsTotal
        entity.deliveryFee = event.deliveryFee
        entity.total = event.productsTotal + event.deliveryFee
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderPaymentCreatedEvent) {
        val entity = getOrder(event.orderId)
        entity.paymentId = event.id
        entity.paymentSessionUrl = event.sessionUrl
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
        entity.events.add(OrderEntity.OrderEvent(OrderEntity.OrderEvent.OrderEventType.CONFIRMED))
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: RestaurantOrderPreparedEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.PREPARED
        entity.events.add(OrderEntity.OrderEvent(OrderEntity.OrderEvent.OrderEventType.PREPARED))
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
        entity.events.add(OrderEntity.OrderEvent(OrderEntity.OrderEvent.OrderEventType.COURIER_ASSIGNED))
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryPickedUpEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.PICKED_UP
        entity.events.add(OrderEntity.OrderEvent(OrderEntity.OrderEvent.OrderEventType.PICKED_UP))
        orderRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryDeliveredEvent) {
        val entity = getOrder(event.orderId)
        entity.status = OrderEntity.OrderStatus.DELIVERED
        entity.events.add(OrderEntity.OrderEvent(OrderEntity.OrderEvent.OrderEventType.DELIVERED))
        orderRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindOrderByIdQuery): Mono<OrderResponse> {
        val order = orderRepository.load(query.orderId) ?: throw OrderNotFoundException(query.orderId)
        val courier = order.courierId?.let { courierRepository.load(it) }
        return Mono.just(mapToResponse(order, courier))
    }

    @QueryHandler
    fun handle(query: FindOrdersByUserIdQuery): Flux<OrderResponse> {
        val orders = orderRepository.loadByUserId(query.userId)
        return orders.map { mapToResponse(it, null) }
    }

    private fun getOrder(orderId: UUID): OrderEntity {
        return orderRepository.load(orderId) ?: throw OrderNotFoundException(orderId)
    }
}
