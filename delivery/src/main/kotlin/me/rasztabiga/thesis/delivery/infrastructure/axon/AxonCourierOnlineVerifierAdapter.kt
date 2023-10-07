package me.rasztabiga.thesis.delivery.infrastructure.axon

import me.rasztabiga.thesis.delivery.domain.command.port.CourierOnlineVerifierPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CourierResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindCourierByIdQuery
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service

@Service
class AxonCourierOnlineVerifierAdapter(
    private val queryGateway: QueryGateway
) : CourierOnlineVerifierPort {

    override fun isCourierOnline(courierId: String): Boolean {
        val courier = queryGateway.query(
            FindCourierByIdQuery(courierId),
            ResponseTypes.instanceOf(CourierResponse::class.java)
        ).join()
        return courier.availability == CourierResponse.Availability.ONLINE
    }
}
