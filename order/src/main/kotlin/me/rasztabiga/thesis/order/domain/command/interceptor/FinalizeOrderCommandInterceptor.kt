package me.rasztabiga.thesis.order.domain.command.interceptor

import me.rasztabiga.thesis.order.domain.command.command.FinalizeOrderCommand
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.BiFunction

@Component
class FinalizeOrderCommandInterceptor(
    private val reactorQueryGateway: ReactorQueryGateway
) : MessageDispatchInterceptor<CommandMessage<*>> {

    override fun handle(messages: List<CommandMessage<*>>): BiFunction<Int, CommandMessage<*>, CommandMessage<*>> {
        return BiFunction { _, commandMessage ->
            val command = commandMessage.payload as? FinalizeOrderCommand

            command?.let { finalizeOrderCommand ->
                // TODO do all in parallel
                val order = getOrder(finalizeOrderCommand.orderId)
                val restaurant = getRestaurant(order.restaurantId)
                val user = getUser(order.userId)
                // TODO validate delivery address id

                validateRestaurant(restaurant, order)
                validateUser(user, finalizeOrderCommand)
            }

            commandMessage
        }
    }

    private fun getOrder(orderId: UUID): OrderResponse {
        return reactorQueryGateway.query(
            FindOrderByIdQuery(orderId),
            ResponseTypes.optionalInstanceOf(OrderResponse::class.java)
        ).share().block()!!.let {
            require(it.isPresent) {
                "Order with id $orderId does not exist"
            }
            it.get()
        }
    }

    private fun getRestaurant(restaurantId: UUID): RestaurantResponse {
        return reactorQueryGateway.query(
            FindRestaurantByIdQuery(restaurantId),
            ResponseTypes.optionalInstanceOf(RestaurantResponse::class.java)
        ).share().block()!!.let {
            require(it.isPresent) {
                "Restaurant with id $restaurantId does not exist"
            }
            it.get()
        }
    }

    private fun getUser(userId: String): UserResponse {
        return reactorQueryGateway.query(
            FindUserByIdQuery(userId),
            ResponseTypes.optionalInstanceOf(UserResponse::class.java)
        ).share().block()!!.let {
            require(it.isPresent) {
                "User with id $userId does not exist"
            }
            it.get()
        }
    }

    private fun validateRestaurant(restaurant: RestaurantResponse, order: OrderResponse) {
        require(restaurant.availability == RestaurantResponse.Availability.OPEN) {
            "Restaurant with id ${order.restaurantId} is closed"
        }

        order.items.forEach { item ->
            require(restaurant.menu.any { it.id == item.productId }) {
                "Product with id ${item.productId} does not exist in restaurant with id ${order.restaurantId}"
            }
        }
    }

    private fun validateUser(user: UserResponse, command: FinalizeOrderCommand) {
        require(user.deliveryAddresses.any { it.id == command.deliveryAddressId }) {
            "Delivery address with id ${command.deliveryAddressId} does not exist for user with id ${command.userId}"
        }
    }
}
