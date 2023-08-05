package me.rasztabiga.thesis.order.domain.command.saga

import me.rasztabiga.thesis.order.domain.command.command.CalculateOrderTotalCommand
import me.rasztabiga.thesis.order.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.OrderTotalCalculatedEvent
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import reactor.core.publisher.Mono

@Saga
class OrderLifecycleSaga(
    private val reactorCommandGateway: ReactorCommandGateway
) {

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderFinalizedEvent): Mono<Void> {
        // TODO calculate order total
        return reactorCommandGateway.send(CalculateOrderTotalCommand(
            orderId = event.orderId,
            restaurantId = event.restaurantId,
            items = event.items.map {
                CalculateOrderTotalCommand.OrderItem(
                    orderItemId = it.orderItemId,
                    productId = it.productId
                )
            }
        ))
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderTotalCalculatedEvent) {
        // TODO 4. create order payment (command)
        println("test")
    }
}
