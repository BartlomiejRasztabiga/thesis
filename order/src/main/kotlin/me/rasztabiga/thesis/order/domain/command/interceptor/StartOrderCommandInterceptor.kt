package me.rasztabiga.thesis.order.domain.command.interceptor

import kotlinx.coroutines.reactive.awaitFirst
import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.domain.query.query.FindRestaurantByIdQuery
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class StartOrderCommandInterceptor(
    private val queryGateway: QueryGateway
) : MessageDispatchInterceptor<CommandMessage<*>> {

    override fun handle(messages: List<CommandMessage<*>>): BiFunction<Int, CommandMessage<*>, CommandMessage<*>> {
        return BiFunction { _, commandMessage ->
            val command = commandMessage.payload as? StartOrderCommand

            command?.let {
                val restaurant = queryGateway.query(
                    FindRestaurantByIdQuery(
                        command.restaurantId,
                    ),
                    ResponseTypes.instanceOf(RestaurantResponse::class.java)
                ).get()

                requireNotNull(restaurant) {
                    "Restaurant with id ${command.restaurantId} does not exist"
                }

                require(restaurant.availability == RestaurantResponse.Availability.OPEN) {
                    "Restaurant with id ${command.restaurantId} is closed"
                }
            }

            commandMessage
        }
    }
}
