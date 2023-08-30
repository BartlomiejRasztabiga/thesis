package me.rasztabiga.thesis.saga.domain.command.saga

import me.rasztabiga.thesis.shared.domain.command.command.CreatePayerCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayerCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

@Saga
@ProcessingGroup("restaurantsaga")
class RestaurantLifecycleSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    private lateinit var payerId: UUID

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    fun on(event: RestaurantCreatedEvent) {
        payerId = UUID.randomUUID()

        commandGateway.sendAndWait<Void>(CreatePayerCommand(id = payerId, userId = event.userId))
    }

    @Suppress("UnusedParameter")
    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    fun on(event: PayerCreatedEvent) {
        // do nothing
    }
}
