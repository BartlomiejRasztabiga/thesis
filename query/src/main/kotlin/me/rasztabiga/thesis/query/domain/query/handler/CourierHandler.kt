package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.query.domain.query.exception.BestCourierNotFoundException
import me.rasztabiga.thesis.query.domain.query.exception.CourierNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.CourierMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.CourierMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.port.DistanceCalculatorPort
import me.rasztabiga.thesis.query.domain.query.repository.CourierRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.shared.domain.command.event.CourierAvailabilityUpdatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.CourierLocationUpdatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindBestCourierForDeliveryQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindCourierByIdQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
@ProcessingGroup("projection")
class CourierHandler(
    private val courierRepository: CourierRepository,
    private val distanceCalculatorPort: DistanceCalculatorPort
) {

    @EventHandler
    fun on(event: CourierCreatedEvent) {
        val entity = mapToEntity(event)
        courierRepository.save(entity)
    }

    @EventHandler
    fun on(event: CourierAvailabilityUpdatedEvent) {
        val entity = getCourier(event.id)
        entity.availability = CourierEntity.Availability.valueOf(event.availability.name)
        courierRepository.save(entity)
    }

    @EventHandler
    fun on(event: CourierLocationUpdatedEvent) {
        val entity = getCourier(event.id)
        entity.location = event.location
        courierRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindCourierByIdQuery): Mono<CourierResponse> {
        val courier = courierRepository.load(query.courierId) ?: throw CourierNotFoundException(query.courierId)
        return Mono.just(mapToResponse(courier))
    }

    @QueryHandler
    fun handle(query: FindBestCourierForDeliveryQuery): Mono<CourierResponse> {
        val couriers = courierRepository.loadAllOnlineWithoutCurrentDelivery()
        val bestCourier = couriers.minByOrNull {
            distanceCalculatorPort.calculateDistance(it.location!!, query.restaurantLocation)
        } ?: throw BestCourierNotFoundException()

        return Mono.just(mapToResponse(bestCourier))
    }

    private fun getCourier(id: String): CourierEntity {
        return courierRepository.load(id) ?: throw CourierNotFoundException(id)
    }
}
