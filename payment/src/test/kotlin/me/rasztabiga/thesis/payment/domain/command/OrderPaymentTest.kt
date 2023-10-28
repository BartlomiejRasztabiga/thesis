package me.rasztabiga.thesis.payment.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.payment.domain.command.aggregate.OrderPayment
import me.rasztabiga.thesis.payment.domain.command.command.PayPaymentCommand
import me.rasztabiga.thesis.payment.domain.command.event.OrderPaymentDeletedEvent
import me.rasztabiga.thesis.payment.domain.command.port.PaymentSessionPort
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.command.DeleteOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentPaidEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class OrderPaymentTest {

    private lateinit var testFixture: AggregateTestFixture<OrderPayment>
    private lateinit var paymentSessionPort: PaymentSessionPort

    @BeforeEach
    fun setUp() {
        paymentSessionPort = mockk<PaymentSessionPort>()

        testFixture = AggregateTestFixture(OrderPayment::class.java)
        testFixture.registerInjectableResource(paymentSessionPort)
    }

    @Test
    fun `should create order payment`() {
        val createOrderPaymentCommand = CreateOrderPaymentCommand(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payerId = "payerId",
            amount = BigDecimal.TEN,
            items = listOf(
                CreateOrderPaymentCommand.OrderItem(
                    name = "name",
                    quantity = 1,
                    unitPrice = BigDecimal.TEN
                )
            ),
            deliveryFee = BigDecimal.ZERO,
        )

        val sessionUrl = "sessionUrl"
        every { paymentSessionPort.createPaymentSession(createOrderPaymentCommand) } returns sessionUrl

        val orderPaymentCreatedEvent = OrderPaymentCreatedEvent(
            id = createOrderPaymentCommand.id,
            orderId = createOrderPaymentCommand.orderId,
            payerId = createOrderPaymentCommand.payerId,
            amount = createOrderPaymentCommand.amount,
            sessionUrl = sessionUrl
        )

        testFixture.givenNoPriorActivity()
            .`when`(createOrderPaymentCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderPaymentCreatedEvent)
    }

    @Test
    fun `given order payment, should pay payment`() {
        val orderPaymentCreatedEvent = OrderPaymentCreatedEvent(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payerId = "payerId",
            amount = BigDecimal.TEN,
            sessionUrl = "sessionUrl"
        )

        val payPaymentCommand = PayPaymentCommand(
            paymentId = orderPaymentCreatedEvent.id
        )

        val orderPaymentPaidEvent = OrderPaymentPaidEvent(
            paymentId = orderPaymentCreatedEvent.id,
            orderId = orderPaymentCreatedEvent.orderId
        )

        testFixture.given(orderPaymentCreatedEvent)
            .`when`(payPaymentCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderPaymentPaidEvent)
    }

    @Test
    fun `given order payment, should delete payment`() {
        val orderPaymentCreatedEvent = OrderPaymentCreatedEvent(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payerId = "payerId",
            amount = BigDecimal.TEN,
            sessionUrl = "sessionUrl"
        )

        val deleteOrderPaymentCommand = DeleteOrderPaymentCommand(
            paymentId = orderPaymentCreatedEvent.id
        )

        val orderPaymentDeletedEvent = OrderPaymentDeletedEvent(
            id = orderPaymentCreatedEvent.id,
        )

        testFixture.given(orderPaymentCreatedEvent)
            .`when`(deleteOrderPaymentCommand)
            .expectSuccessfulHandlerExecution()
            .expectMarkedDeleted()
            .expectEvents(orderPaymentDeletedEvent)
    }
}
