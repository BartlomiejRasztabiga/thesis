package me.rasztabiga.thesis.saga.domain.command.saga

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.PayeeResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.command.command.AddPayeeBalanceCommand
import me.rasztabiga.thesis.shared.domain.command.command.CalculateOrderTotalCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateInvoiceCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderDeliveryOfferCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.command.DeleteOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.command.MarkOrderAsDeliveredCommand
import me.rasztabiga.thesis.shared.domain.command.command.MarkOrderAsPaidCommand
import me.rasztabiga.thesis.shared.domain.command.command.RejectOrderCommand
import me.rasztabiga.thesis.shared.domain.command.command.SendInvoiceEmailCommand
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceEmailSentEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryDeliveredEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderTotalCalculatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderRejectedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindCourierByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderDeliveryByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindPayeeByUserIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.*

@Suppress("TooManyFunctions")
@Saga
@ProcessingGroup("ordersaga")
class OrderLifecycleSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    @Autowired
    @Transient
    private lateinit var queryGateway: QueryGateway

    private lateinit var orderingUserId: String
    private lateinit var paymentId: UUID
    private lateinit var restaurantOrderId: UUID
    private lateinit var deliveryId: UUID
    private lateinit var restaurantId: UUID
    private lateinit var orderId: UUID
    private lateinit var courierId: String

    private lateinit var restaurantManagerPayeeId: UUID
    private lateinit var deliveryCourierPayeeId: UUID

    private lateinit var userInvoiceId: UUID

    private var userInvoiceSent: Boolean = false

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderFinalizedEvent) {
        orderingUserId = event.userId
        restaurantId = event.restaurantId
        orderId = event.orderId

        val order = getOrder(event.orderId)
        val user = getUser(event.userId)

        val deliveryLocation = user.deliveryAddresses.find { it.id == user.defaultAddressId }?.location
        // little hack, because OrderFinalizedEvent might not be yet handled by query service

        commandGateway.sendAndWait<Void>(
            CalculateOrderTotalCommand(
                orderId = event.orderId,
                restaurantId = event.restaurantId,
                items = event.items,
                restaurantAddress = order.restaurantLocation.streetAddress!!,
                deliveryAddress = deliveryLocation!!.streetAddress!!
            )
        )
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException", "UnusedParameter")
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderCanceledEvent) {
        try {
            commandGateway.sendAndWait<Void>(
                DeleteOrderPaymentCommand(
                    paymentId = paymentId
                )
            )
        } catch (e: Exception) {
            // TODO payment not found
            // ignore
        }

    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderTotalCalculatedEvent) {
        val paymentId = UUID.randomUUID()

        val order = getOrder(event.orderId)
        val restaurant = getRestaurant(order.restaurantId)

        commandGateway.sendAndWait<Void>(
            CreateOrderPaymentCommand(
                id = paymentId,
                orderId = event.orderId,
                payerId = this.orderingUserId,
                amount = event.productsTotal + event.deliveryFee,
                items = order.items.map {
                    val menuItem = restaurant.menu.find { menuItem -> menuItem.id == it.key }!!
                    CreateOrderPaymentCommand.OrderItem(
                        name = menuItem.name,
                        quantity = it.value,
                        unitPrice = menuItem.price
                    )
                },
                deliveryFee = event.deliveryFee
            )
        )
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderPaymentPaidEvent) {
        commandGateway.sendAndWait<Void>(
            MarkOrderAsPaidCommand(
                orderId = event.orderId,
                userId = this.orderingUserId
            )
        )
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderPaidEvent) {
        val order = getOrder(event.orderId)

        restaurantOrderId = UUID.randomUUID()

        // TODO aggregate id must be unique?
        // TODO associate restaurantOrderId with saga?
        commandGateway.sendAndWait<Void>(
            CreateRestaurantOrderCommand(
                restaurantOrderId = restaurantOrderId,
                orderId = event.orderId,
                restaurantId = order.restaurantId,
                items = order.items
            )
        )
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: RestaurantOrderAcceptedEvent) {
        val order = getOrder(event.orderId)
        val user = getUser(order.userId)
        val restaurant = getRestaurant(order.restaurantId)

        val deliveryAddress = user.deliveryAddresses.find { it.id == order.deliveryAddressId }
        checkNotNull(deliveryAddress) { "Delivery address not found" }

        deliveryId = UUID.randomUUID()

        try {
            commandGateway.sendAndWait<Void>(
                CreateOrderDeliveryOfferCommand(
                    id = deliveryId,
                    orderId = event.orderId,
                    restaurantLocation = restaurant.location,
                    deliveryLocation = deliveryAddress.location
                )
            )
        } catch (e: Exception) {
            // TODO handle exception, retry?
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: RestaurantOrderRejectedEvent) {
        try {
            commandGateway.sendAndWait<Void>(
                DeleteOrderPaymentCommand(
                    paymentId = paymentId
                )
            )
        } catch (e: Exception) {
            // TODO payment not found
            // ignore
        }

        commandGateway.sendAndWait<Void>(
            RejectOrderCommand(
                orderId = event.orderId
            )
        )
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderDeliveryDeliveredEvent) {
        courierId = event.courierId

        commandGateway.sendAndWait<Void>(
            MarkOrderAsDeliveredCommand(
                orderId = event.orderId
            )
        )

        val restaurant = getRestaurant(restaurantId)
        val order = getOrder(event.orderId)

        val restaurantManagerPayee = getPayeeByUserId(restaurant.managerId)

        restaurantManagerPayeeId = restaurantManagerPayee.id

        SagaLifecycle.associateWith("restaurantManagerPayeeId", restaurantManagerPayeeId.toString())

        commandGateway.sendAndWait<Void>(
            AddPayeeBalanceCommand(
                payeeId = restaurantManagerPayeeId,
                amount = order.itemsTotal!!
            )
        )

        val deliveryCourierPayee = getPayeeByUserId(order.courierId!!)
        val delivery = getDelivery(event.deliveryId)

        deliveryCourierPayeeId = deliveryCourierPayee.id

        SagaLifecycle.associateWith("deliveryCourierPayeeId", deliveryCourierPayeeId.toString())

        commandGateway.sendAndWait<Void>(
            AddPayeeBalanceCommand(
                payeeId = deliveryCourierPayeeId,
                amount = delivery.courierFee
            )
        )

        val user = getUser(order.userId)

        userInvoiceId = UUID.randomUUID()
        SagaLifecycle.associateWith("userInvoiceId", userInvoiceId.toString())

        commandGateway.sendAndWait<Void>(
            CreateInvoiceCommand(
                id = userInvoiceId,
                from = "Food Delivery App",
                to = user.name,
                issueDate = LocalDate.now(),
                dueDate = LocalDate.now(),
                items = order.items.map {
                    val menuItem = restaurant.menu.find { menuItem -> menuItem.id == it.key }!!

                    CreateInvoiceCommand.InvoiceItem(
                        name = menuItem.name,
                        quantity = it.value,
                        unitPrice = menuItem.price
                    )
                }.plus(
                    CreateInvoiceCommand.InvoiceItem(
                        name = "Delivery fee",
                        quantity = 1,
                        unitPrice = order.deliveryFee!!
                    )
                ),
                amountPaid = (order.itemsTotal!! + order.deliveryFee!!).toString(),
            )
        )
    }

    @Suppress("UnusedParameter")
    @SagaEventHandler(associationProperty = "invoiceId", keyName = "userInvoiceId")
    fun on(event: InvoiceCreatedEvent) {
        val user = getUser(orderingUserId)

        commandGateway.sendAndWait<Void>(
            SendInvoiceEmailCommand(
                id = userInvoiceId,
                email = user.email
            )
        )
    }

    @Suppress("UnusedParameter")
    @EndSaga
    @SagaEventHandler(associationProperty = "invoiceId", keyName = "userInvoiceId")
    fun on(event: InvoiceEmailSentEvent) {
        userInvoiceSent = true
    }

    private fun getOrder(orderId: UUID): OrderResponse {
        return queryGateway.query(
            FindOrderByIdQuery(orderId), ResponseTypes.instanceOf(OrderResponse::class.java)
        ).join()
    }

    private fun getUser(userId: String): UserResponse {
        return queryGateway.query(
            FindUserByIdQuery(userId), ResponseTypes.instanceOf(UserResponse::class.java)
        ).join()
    }

    private fun getRestaurant(restaurantId: UUID): RestaurantResponse {
        return queryGateway.query(
            FindRestaurantByIdQuery(restaurantId), ResponseTypes.instanceOf(RestaurantResponse::class.java)
        ).join()
    }

    private fun getPayeeByUserId(userId: String): PayeeResponse {
        return queryGateway.query(
            FindPayeeByUserIdQuery(userId), ResponseTypes.instanceOf(PayeeResponse::class.java)
        ).join()
    }

    private fun getDelivery(deliveryId: UUID): OrderDeliveryResponse {
        return queryGateway.query(
            FindOrderDeliveryByIdQuery(deliveryId), ResponseTypes.instanceOf(OrderDeliveryResponse::class.java)
        ).join()
    }


}
