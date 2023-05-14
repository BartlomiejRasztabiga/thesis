package me.rasztabiga.thesis.order.domain.command.interceptor

import me.rasztabiga.thesis.order.domain.command.command.StartOrderCommand
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.MessageDispatchInterceptor
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
                // TODO
//                reactorQueryGateway.query(
//                    FindRestaurantByIdQuery(
//                        command.restaurantId,
//                    ),
//                    ResponseTypes.instanceOf(RestaurantResponse::class.java)
//                )
            }

            commandMessage
        }
    }
}
