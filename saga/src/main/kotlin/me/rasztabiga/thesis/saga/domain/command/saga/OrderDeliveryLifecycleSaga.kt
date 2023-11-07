package me.rasztabiga.thesis.saga.domain.command.saga

import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAssignedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Suppress("TooManyFunctions")
@Saga
@ProcessingGroup("orderdelivery")
class OrderDeliveryLifecycleSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    @Autowired
    @Transient
    private lateinit var queryGateway: QueryGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "deliveryId")
    fun on(event: OrderDeliveryCreatedEvent) {
        val
    }

    //    @EndSaga
    @SagaEventHandler(associationProperty = "deliveryId")
    fun on(event: OrderDeliveryAssignedEvent) {

    }
}
