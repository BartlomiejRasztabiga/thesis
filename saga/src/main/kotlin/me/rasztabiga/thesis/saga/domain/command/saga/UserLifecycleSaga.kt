package me.rasztabiga.thesis.saga.domain.command.saga

import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
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
@ProcessingGroup("usersaga")
class UserLifecycleSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    private lateinit var payeeId: UUID

    @StartSaga
    @SagaEventHandler(associationProperty = "userId")
    fun on(event: UserCreatedEvent) {
        payeeId = UUID.randomUUID()

        commandGateway.sendAndWait<Void>(CreatePayeeCommand(id = payeeId, userId = event.userId))
    }

    @Suppress("UnusedParameter")
    @EndSaga
    @SagaEventHandler(associationProperty = "userId")
    fun on(event: PayeeCreatedEvent) {
        // do nothing
    }
}
