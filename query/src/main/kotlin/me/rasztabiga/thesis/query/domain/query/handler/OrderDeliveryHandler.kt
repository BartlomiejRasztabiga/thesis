package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.query.domain.query.entity.DeliveryStatus
import me.rasztabiga.thesis.query.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.query.domain.query.exception.DeliveryNotFoundException
import me.rasztabiga.thesis.query.domain.query.exception.SuitableDeliveryOfferNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.OrderDeliveryMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.OrderDeliveryMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.query.domain.query.query.FindCurrentDeliveryQuery
import me.rasztabiga.thesis.query.domain.query.query.FindSuitableDeliveryOfferQuery
import me.rasztabiga.thesis.query.domain.query.repository.OrderDeliveryRepository
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryPickedUpEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryRejectedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
@ProcessingGroup("orderdelivery")
class OrderDeliveryHandler(
    private val orderDeliveryRepository: OrderDeliveryRepository,
    private val distanceCalculatorPort: DistanceCalculatorPort
) {

    @EventHandler
    fun on(event: OrderDeliveryCreatedEvent) {
        val entity = mapToEntity(event)
        orderDeliveryRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryRejectedEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.courierIdsDeclined.add(event.courierId)
        orderDeliveryRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryAcceptedEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.courierId = event.courierId
        entity.status = DeliveryStatus.ACCEPTED
        orderDeliveryRepository.save(entity)
    }

    @EventHandler
    fun on(event: OrderDeliveryPickedUpEvent) {
        val entity = getDelivery(event.deliveryId)
        entity.status = DeliveryStatus.PICKED_UP
        orderDeliveryRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindSuitableDeliveryOfferQuery): Mono<OrderDeliveryResponse> {
        val offers = orderDeliveryRepository.loadOffers().filter { !it.courierIdsDeclined.contains(query.courierId) }
        if (offers.isEmpty()) {
            return Mono.error(SuitableDeliveryOfferNotFoundException())
        }

        val bestOffer = offers.minBy {
            distanceCalculatorPort.calculateDistance(query.courierAddress, it.restaurantAddress)
        }

        return Mono.just(mapToResponse(bestOffer))
    }

    @QueryHandler
    fun handle(query: FindCurrentDeliveryQuery): Mono<OrderDeliveryResponse> {
        return orderDeliveryRepository.loadCurrentDeliveryByCourierId(query.courierId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(DeliveryNotFoundException())
    }

    private fun getDelivery(deliveryId: UUID): OrderDeliveryEntity {
        return orderDeliveryRepository.load(deliveryId) ?: throw DeliveryNotFoundException(deliveryId)
    }
}
