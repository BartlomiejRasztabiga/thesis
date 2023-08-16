package me.rasztabiga.thesis.order.domain.command.saga

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.order.domain.command.command.MarkOrderAsPaidCommand
import me.rasztabiga.thesis.order.domain.command.command.RejectOrderCommand
import me.rasztabiga.thesis.order.domain.command.event.OrderCanceledEvent
import me.rasztabiga.thesis.order.domain.command.event.OrderFinalizedEvent
import me.rasztabiga.thesis.order.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.command.command.CalculateOrderTotalCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderDeliveryOfferCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreateRestaurantOrderCommand
import me.rasztabiga.thesis.shared.domain.command.command.DeleteOrderPaymentCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderPaymentPaidEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderTotalCalculatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderPreparedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderRejectedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Saga
@ProcessingGroup("ordersaga")
class OrderLifecycleSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    @Autowired
    @Transient
    private lateinit var queryGateway: QueryGateway

    private lateinit var userId: String
    private lateinit var paymentId: UUID
    private lateinit var restaurantOrderId: UUID
    private lateinit var deliveryId: UUID

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

        commandGateway.sendAndWait<Void>(
            CreateOrderPaymentCommand(
                id = paymentId,
                orderId = event.orderId,
                payeeId = this.userId,
                amount = event.total
            )
        )
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: OrderPaymentPaidEvent) {
        commandGateway.sendAndWait<Void>(
            MarkOrderAsPaidCommand(
                orderId = event.orderId,
                userId = this.userId
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
                items = order.items.map {
                    CreateRestaurantOrderCommand.OrderItem(
                        productId = it.productId
                    )
                }
            )
        )
    }

    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: RestaurantOrderAcceptedEvent) {
        val order = getOrder(event.orderId)
        val user = getUser(order.userId)
        val restaurant = getRestaurant(order.restaurantId)

        val deliveryAddress = user.deliveryAddresses.find { it.id == order.deliveryAddressId }
            ?: throw RuntimeException("Delivery address not found")

        deliveryId = UUID.randomUUID()

        commandGateway.sendAndWait<Void>(
            CreateOrderDeliveryOfferCommand(
                id = deliveryId,
                orderId = event.orderId,
                restaurantAddress = restaurant.address,
                deliveryAddress = deliveryAddress.address
            )
        )
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

    @Suppress("UnusedParameter")
    @SagaEventHandler(associationProperty = "orderId")
    fun on(event: RestaurantOrderPreparedEvent) {
        println("RestaurantOrderPreparedEvent")
        // TODO create order delivery
    }

    // TODO end saga on order delivered?

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
}
