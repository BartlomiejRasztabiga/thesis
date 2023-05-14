package me.rasztabiga.thesis.order.config

import me.rasztabiga.thesis.order.domain.command.interceptor.StartOrderCommandInterceptor
import org.axonframework.commandhandling.CommandBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan
class CommandConfiguration(
    private val startOrderCommandInterceptor: StartOrderCommandInterceptor
) {

    @Autowired
    fun registerCommandInterceptors(commandBus: CommandBus) {
        commandBus.registerDispatchInterceptor(startOrderCommandInterceptor)
    }
}
