package me.rasztabiga.thesis.order.domain.command.saga

import me.rasztabiga.thesis.order.domain.command.command.CalculateOrderTotalCommand
import me.rasztabiga.thesis.order.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.restaurant.domain.command.event.OrderTotalCalculatedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@ProcessingGroup("ordersaga")
class OrderLifecycleSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderFinalizedEvent) {
        // TODO calculate order total
        commandGateway.sendAndWait<Void>(CalculateOrderTotalCommand(
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

    @Suppress("UnusedParameter") // TODO delete
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderTotalCalculatedEvent) {
        // TODO 4. create order payment (command)
        println("test")
    }
}
