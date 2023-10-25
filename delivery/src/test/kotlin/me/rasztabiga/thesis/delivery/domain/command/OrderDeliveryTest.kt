package me.rasztabiga.thesis.delivery.domain.command

import io.mockk.every
import io.mockk.mockk
import me.rasztabiga.thesis.delivery.domain.command.aggregate.OrderDelivery
import me.rasztabiga.thesis.delivery.domain.command.port.CalculateDeliveryFeePort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderDeliveryOfferCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class OrderDeliveryTest {

    private lateinit var testFixture: AggregateTestFixture<OrderDelivery>
    private lateinit var calculateDeliveryFeePort: CalculateDeliveryFeePort

    @BeforeEach
    fun setUp() {
        calculateDeliveryFeePort = mockk<CalculateDeliveryFeePort>()

        testFixture = AggregateTestFixture(OrderDelivery::class.java)
        testFixture.registerInjectableResource(calculateDeliveryFeePort)
    }

    @Test
    fun `should create order delivery offer`() {
        every { calculateDeliveryFeePort.calculateBaseFee(any(), any()) } returns BigDecimal(10)

        val createOrderDeliveryCommand = CreateOrderDeliveryOfferCommand(
            id = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            restaurantLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street"),
            deliveryLocation = Location(lat = 1.0, lng = 1.0, streetAddress = "street")
        )

        val orderDeliveryCreatedEvent = OrderDeliveryCreatedEvent(
            deliveryId = createOrderDeliveryCommand.id,
            orderId = createOrderDeliveryCommand.orderId,
            restaurantLocation = createOrderDeliveryCommand.restaurantLocation,
            deliveryLocation = createOrderDeliveryCommand.deliveryLocation,
            courierFee = BigDecimal(10)
        )

        testFixture.givenNoPriorActivity()
            .`when`(createOrderDeliveryCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(orderDeliveryCreatedEvent)

    }
}
