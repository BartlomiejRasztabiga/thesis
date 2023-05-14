package me.rasztabiga.thesis.order.domain.command.aggregate

import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
internal class Order {
    @AggregateIdentifier
    private lateinit var id: UUID

    private constructor()

    constructor(command: StartOrderCommand) {
        apply(OrderStartedEvent(orderId = command.orderId))
    }

    @EventSourcingHandler
    fun on(event: OrderStartedEvent) {
        this.id = event.orderId
    }
}
