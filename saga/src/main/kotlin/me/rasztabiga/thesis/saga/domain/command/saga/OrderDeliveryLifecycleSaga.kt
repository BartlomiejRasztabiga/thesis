package me.rasztabiga.thesis.saga.domain.command.saga

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderDeliveryResponse
import me.rasztabiga.thesis.shared.domain.command.command.AssignDeliveryCommand
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryAcceptedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.OrderDeliveryRejectedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindBestCourierForDeliveryQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderDeliveryByIdQuery
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

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
        // TODO handle exception?
        val courier = findBestCourier(event.restaurantLocation) ?: error("Cannot find best courier for delivery")

        // TODO handle exception?
        commandGateway.sendAndWait<Any>(AssignDeliveryCommand(event.deliveryId, courier.id))
    }

    // TODO handle rejected
    @Suppress("UnusedParameter")
    @SagaEventHandler(associationProperty = "deliveryId")
    fun on(event: OrderDeliveryRejectedEvent) {
        // TODO handle exception?
        val courier = findBestCourier(event.restaurantLocation) ?: error("Cannot find best courier for delivery")

        // TODO handle exception?
        commandGateway.sendAndWait<Any>(AssignDeliveryCommand(event.deliveryId, courier.id))
    }

    // TODO handle accepted?

    @Suppress("UnusedParameter")
    @EndSaga
    @SagaEventHandler(associationProperty = "deliveryId")
    fun on(event: OrderDeliveryAcceptedEvent) {
        // TODO check if courier doesnt' have two deliveries?
        // if so, unassign one of them (leaving only one) xD
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException", "MagicNumber")
    private fun findBestCourier(restaurantLocation: Location): CourierResponse? {
        var retries = 0
        val waitTimeInSeconds = 5
        val waitMultiplier = 2
        var currentWait = waitTimeInSeconds
        while (true) {
            retries++
            if (retries > 10) {
                // TODO handle
                error("Cannot find best courier for delivery")
            }
            try {
                val query = FindBestCourierForDeliveryQuery(
                    restaurantLocation = restaurantLocation
                )
                return queryGateway.query(query, ResponseTypes.instanceOf(CourierResponse::class.java)).join()
            } catch (e: Exception) {
                Thread.sleep(currentWait * 1000L)
                currentWait *= waitMultiplier
            }
        }
    }
}
