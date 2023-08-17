package me.rasztabiga.thesis.delivery.domain.query.handler

import me.rasztabiga.thesis.delivery.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.delivery.domain.query.mapper.OrderDeliveryMapper.mapToEntity
import me.rasztabiga.thesis.delivery.domain.query.repository.OrderDeliveryRepository
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
@ProcessingGroup("orderdelivery")
class OrderDeliveryHandler(
    private val orderDeliveryRepository: OrderDeliveryRepository
) {

    @EventHandler
    fun on(event: OrderDeliveryCreatedEvent) {
        val entity = mapToEntity(event)
        orderDeliveryRepository.save(entity)
    }
}
