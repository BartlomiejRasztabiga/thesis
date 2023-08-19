package me.rasztabiga.thesis.delivery.infrastructure.axon

import me.rasztabiga.thesis.delivery.domain.command.port.OrderPreparedVerifierPort
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*

@Service
class AxonOrderPreparedVerifierAdapter(
    private val queryGateway: QueryGateway
) : OrderPreparedVerifierPort {
    override fun isOrderPrepared(orderId: UUID): Boolean {
        return queryGateway.query(
            FindOrder(
                orderId = orderId
            ),
            Boolean::class.java
        ).join()
    }
}
