package me.rasztabiga.thesis.delivery.domain.query.handler

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.delivery.domain.command.event.CourierCreatedEvent
import me.rasztabiga.thesis.delivery.domain.query.exception.CourierNotFoundException
import me.rasztabiga.thesis.delivery.domain.query.mapper.CourierMapper.mapToEntity
import me.rasztabiga.thesis.delivery.domain.query.mapper.CourierMapper.mapToResponse
import me.rasztabiga.thesis.delivery.domain.query.query.FindCourierByIdQuery
import me.rasztabiga.thesis.delivery.domain.query.repository.CourierRepository
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

    @QueryHandler
    fun handle(query: FindCourierByIdQuery): Mono<CourierResponse> {
        return courierRepository.load(query.courierId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(CourierNotFoundException(query.courierId))
    }
}
