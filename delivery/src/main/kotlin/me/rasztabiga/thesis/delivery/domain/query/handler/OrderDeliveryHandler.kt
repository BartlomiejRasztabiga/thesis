package me.rasztabiga.thesis.delivery.domain.query.handler

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.delivery.domain.command.command.RejectDeliveryOfferCommand
import me.rasztabiga.thesis.delivery.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.delivery.domain.command.event.OrderDeliveryRejectedEvent
import me.rasztabiga.thesis.delivery.domain.query.entity.OrderDeliveryEntity
import me.rasztabiga.thesis.delivery.domain.query.exception.DeliveryNotFoundException
import me.rasztabiga.thesis.delivery.domain.query.exception.SuitableDeliveryOfferNotFoundException
import me.rasztabiga.thesis.delivery.domain.query.mapper.OrderDeliveryMapper.mapToEntity
import me.rasztabiga.thesis.delivery.domain.query.mapper.OrderDeliveryMapper.mapToResponse
import me.rasztabiga.thesis.delivery.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.delivery.domain.query.query.FindSuitableDeliveryOfferQuery
import me.rasztabiga.thesis.delivery.domain.query.repository.OrderDeliveryRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.UUID

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

    private fun getDelivery(deliveryId: UUID): OrderDeliveryEntity {
        return orderDeliveryRepository.load(deliveryId) ?: throw DeliveryNotFoundException(deliveryId)
    }
}
