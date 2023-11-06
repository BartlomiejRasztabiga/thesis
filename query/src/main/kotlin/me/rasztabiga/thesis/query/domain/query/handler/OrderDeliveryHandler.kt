package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.query.domain.query.exception.CourierLocationNotSetException
import me.rasztabiga.thesis.query.domain.query.exception.CourierNotFoundException
import me.rasztabiga.thesis.query.domain.query.exception.DeliveryNotFoundException
import me.rasztabiga.thesis.query.domain.query.exception.SuitableDeliveryOfferNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.OrderDeliveryMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.OrderDeliveryMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.query.domain.query.query.FindAllDeliveriesByCourierId
import me.rasztabiga.thesis.query.domain.query.query.FindCurrentDeliveryQuery
import me.rasztabiga.thesis.query.domain.query.repository.CourierRepository
import me.rasztabiga.thesis.query.domain.query.repository.OrderDeliveryRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAssignedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryPickedUpEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryRejectedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderDeliveryByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindSuitableDeliveryOfferQuery
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
class OrderDeliveryHandler(
    private val orderDeliveryRepository: OrderDeliveryRepository,
    private val courierRepository: CourierRepository,
    private val distanceCalculatorPort: DistanceCalculatorPort
) {

    @EventHandler
    fun on(event: OrderDeliveryCreatedEvent) {
        val entity = mapToEntity(event)
        orderDeliveryRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryAssignedEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.courierId = event.courierId
        entity.status = DeliveryStatus.ASSIGNED
        entity.locked = true
        orderDeliveryRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryRejectedEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.courierId = null
        entity.status = DeliveryStatus.OFFER
        entity.locked = false
        orderDeliveryRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryAcceptedEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.courierId = event.courierId
        entity.status = DeliveryStatus.ACCEPTED
        entity.locked = false
        orderDeliveryRepository.save(entity)
    }


    @EventHandler
    fun on(event: OrderDeliveryPickedUpEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.status = DeliveryStatus.PICKED_UP
        orderDeliveryRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryDeliveredEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.status = DeliveryStatus.DELIVERED
        orderDeliveryRepository.save(entity)
    }

    @Suppress("ReturnCount")
    @QueryHandler
    fun handle(query: FindSuitableDeliveryOfferQuery): Mono<OrderDeliveryResponse> {
        val courier = getCourier(query.courierId)
        if (courier.location == null) {
            return Mono.error(CourierLocationNotSetException())
        }

        val offers = orderDeliveryRepository.loadOffers()
        if (offers.isEmpty()) {
            return Mono.error(SuitableDeliveryOfferNotFoundException())
        }

        val bestOffer = offers.minBy {
            distanceCalculatorPort.calculateDistance(courier.location!!, it.restaurantLocation)
        }

        val distanceToRestaurant =
            distanceCalculatorPort.calculateDistance(courier.location!!, bestOffer.restaurantLocation)
        val distanceToDeliveryAddress =
            distanceCalculatorPort.calculateDistance(bestOffer.restaurantLocation, bestOffer.deliveryLocation)

        bestOffer.locked = true
        orderDeliveryRepository.save(bestOffer)

        val response = mapToResponse(
            bestOffer,
            distanceToRestaurant,
            distanceToDeliveryAddress
        )

        return Mono.just(response)
    }

    @QueryHandler
    fun handle(query: FindCurrentDeliveryQuery): Mono<OrderDeliveryResponse> {
        val courier = getCourier(query.courierId)

        val delivery = orderDeliveryRepository.loadCurrentDeliveryByCourierId(query.courierId)
            ?: return Mono.error(DeliveryNotFoundException())

        val distanceToRestaurant =
            distanceCalculatorPort.calculateDistance(courier.location!!, delivery.restaurantLocation)
        val distanceToDeliveryAddress =
            distanceCalculatorPort.calculateDistance(delivery.restaurantLocation, delivery.deliveryLocation)

        return Mono.just(mapToResponse(delivery, distanceToRestaurant, distanceToDeliveryAddress))
    }

    @QueryHandler
    fun handle(query: FindOrderDeliveryByIdQuery): Mono<OrderDeliveryResponse> {
        return orderDeliveryRepository.load(query.deliveryId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(DeliveryNotFoundException())
    }

    @QueryHandler
    fun handle(query: FindAllDeliveriesByCourierId): Flux<OrderDeliveryResponse> {
        return orderDeliveryRepository.loadAllByCourierId(query.courierId)
            .map { mapToResponse(it) }
    }

    private fun getDelivery(deliveryId: UUID): OrderDeliveryEntity {
        return orderDeliveryRepository.load(deliveryId) ?: throw DeliveryNotFoundException(deliveryId)
    }

    private fun getCourier(courierId: String): CourierEntity {
        return courierRepository.load(courierId) ?: throw CourierNotFoundException(courierId)
    }
}
