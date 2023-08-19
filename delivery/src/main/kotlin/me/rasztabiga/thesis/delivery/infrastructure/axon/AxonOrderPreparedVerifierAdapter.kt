package me.rasztabiga.thesis.delivery.infrastructure.axon

import me.rasztabiga.thesis.delivery.domain.command.port.OrderPreparedVerifierPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindOrderByIdQuery
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*

@Service
class AxonOrderPreparedVerifierAdapter(
    private val queryGateway: QueryGateway
) : OrderPreparedVerifierPort {
    override fun isOrderPrepared(orderId: UUID): Boolean {
        val order =
            queryGateway.query(FindOrderByIdQuery(orderId), ResponseTypes.instanceOf(OrderResponse::class.java)).join()
        return order.status == OrderResponse.OrderStatus.PREPARED
    }
}
