package me.rasztabiga.thesis.saga.domain.command.saga

import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.event.CourierCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantCreatedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Saga
@ProcessingGroup("couriersaga")
class DeliveryCourierLifecycleSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    private lateinit var payeeId: UUID

    @StartSaga
    @SagaEventHandler(associationProperty = "courierId")
    fun on(event: CourierCreatedEvent) {
        payeeId = UUID.randomUUID()

        SagaLifecycle.associateWith("payeeId", payeeId.toString())

        commandGateway.sendAndWait<Void>(
            CreatePayeeCommand(
                id = payeeId,
                userId = event.courierId,
                name = event.name,
                email = event.email
            )
        )
    }

    @Suppress("UnusedParameter")
    @EndSaga
    @SagaEventHandler(associationProperty = "payeeId")
    fun on(event: PayeeCreatedEvent) {
        // do nothing
    }
}
