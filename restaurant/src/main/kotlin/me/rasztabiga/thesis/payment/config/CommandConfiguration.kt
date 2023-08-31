package me.rasztabiga.thesis.payment.config

import org.axonframework.commandhandling.CommandBus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan
class CommandConfiguration {

    @Autowired
    @Suppress("EmptyFunctionBlock", "UnusedParameter")
    fun registerCommandInterceptors(commandBus: CommandBus) {

    }
}
