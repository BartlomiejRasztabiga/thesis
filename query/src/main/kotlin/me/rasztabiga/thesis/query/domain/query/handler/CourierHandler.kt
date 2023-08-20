package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.exception.CourierNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.CourierMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.CourierMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.repository.CourierRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
@ProcessingGroup("courier")
class CourierHandler(
    private val courierRepository: CourierRepository
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

    @QueryHandler
    fun handle(query: FindCourierByIdQuery): Mono<CourierResponse> {
        return courierRepository.load(query.courierId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(CourierNotFoundException(query.courierId))
    }

    private fun getCourier(id: String): CourierEntity {
        return courierRepository.load(id) ?: throw CourierNotFoundException(id)
    }
}
