package me.rasztabiga.thesis.order.domain.command.interceptor

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
//            val command = commandMessage.payload
//
//            reactorQueryGateway.query(
//                GetDeliveryAddressQuery(
//                    userId = command.userId,
//                    addressId = command.addressId
//                ),
//                DeliveryAddress::class.java
//            ).blockOptional().ifPresent { deliveryAddress ->
//                commandMessage.andMetaData(mapOf("deliveryAddress" to deliveryAddress))
//            }
//
            commandMessage
        }
    }
}
