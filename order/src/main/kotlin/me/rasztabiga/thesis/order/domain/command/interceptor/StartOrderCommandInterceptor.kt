package me.rasztabiga.thesis.order.domain.command.interceptor

import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class StartOrderCommandInterceptor(
    private val reactorQueryGateway: ReactorQueryGateway
) : MessageDispatchInterceptor<CommandMessage<*>> {

    override fun handle(messages: List<CommandMessage<*>>): BiFunction<Int, CommandMessage<*>, CommandMessage<*>> {
        return BiFunction { _, commandMessage ->
            val command = commandMessage.payload as? StartOrderCommand

            command?.let {
                // TODO do both in parallel
                requireRestaurant(it)
                requireUser(it)
            }

            commandMessage
        }
    }

    private fun requireRestaurant(command: StartOrderCommand) {
        val restaurant = reactorQueryGateway.query(
            FindRestaurantByIdQuery(
                command.restaurantId,
            ),
            ResponseTypes.optionalInstanceOf(RestaurantResponse::class.java)
        ).share().block()!!

        require(restaurant.isPresent) {
            "Restaurant with id ${command.restaurantId} does not exist"
        }

        require(restaurant.get().availability == RestaurantResponse.Availability.OPEN) {
            "Restaurant with id ${command.restaurantId} is closed"
        }
    }

    private fun requireUser(command: StartOrderCommand) {
        val user = reactorQueryGateway.query(
            FindUserByIdQuery(
                command.userId,
            ),
            ResponseTypes.optionalInstanceOf(UserResponse::class.java)
        ).share().block()!!

        require(user.isPresent) {
            "User with id ${command.userId} does not exist"
        }
    }
}
