package me.rasztabiga.thesis.order.domain.command.saga

import me.rasztabiga.thesis.order.domain.command.command.MarkOrderAsPaidCommand
import me.rasztabiga.thesis.order.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.command.command.CalculateOrderTotalCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderTotalCalculatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PaymentPaidEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Saga
@ProcessingGroup("ordersaga")
class OrderLifecycleSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    private lateinit var userId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderFinalizedEvent) {
        userId = event.userId

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

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderTotalCalculatedEvent) {
        commandGateway.sendAndWait<Void>(
            CreateOrderPaymentCommand(
                id = UUID.randomUUID(),
                orderId = event.orderId,
                payeeId = this.userId,
                amount = event.total
            )
        )
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: PaymentPaidEvent) {
        commandGateway.sendAndWait<Void>(
            MarkOrderAsPaidCommand(
                orderId = event.orderId,
                userId = this.userId
            )
        )
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderPaidEvent) {
        commandGateway.sendAndWait<Void>(
            CreateRestaurantOrderCommand(
                orderId = event.orderId
            )
        )
    }

    // TODO end saga
}
