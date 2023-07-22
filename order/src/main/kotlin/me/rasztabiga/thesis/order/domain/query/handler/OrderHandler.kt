package me.rasztabiga.thesis.order.domain.query.handler

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import me.rasztabiga.thesis.order.domain.query.exception.OrderNotFoundException
import me.rasztabiga.thesis.order.domain.query.mapper.OrderMapper.mapToEntity
import me.rasztabiga.thesis.order.domain.query.mapper.OrderMapper.mapToResponse
import me.rasztabiga.thesis.order.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.order.domain.query.repository.OrderRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
@ProcessingGroup("order")
class OrderHandler(
    private val orderRepository: OrderRepository
) {

    @EventHandler
    fun on(event: OrderStartedEvent) {
        val entity = mapToEntity(event)
        orderRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindOrderByIdQuery): Mono<OrderResponse> {
        return orderRepository.load(query.orderId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(OrderNotFoundException(query.orderId))
    }
}
