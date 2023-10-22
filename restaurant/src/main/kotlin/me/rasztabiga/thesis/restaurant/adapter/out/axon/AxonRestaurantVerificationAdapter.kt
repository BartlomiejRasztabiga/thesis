package me.rasztabiga.thesis.restaurant.adapter.out.axon

import me.rasztabiga.thesis.restaurant.domain.command.port.RestaurantVerificationPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByManagerIdQuery
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service

@Service
class AxonRestaurantVerificationAdapter(
    private val queryGateway: QueryGateway
) : RestaurantVerificationPort {

    override fun verifyRestaurantByManagerIdExists(managerId: String): Boolean {
        return queryGateway.query(
            FindRestaurantByManagerIdQuery(
                managerId,
            ),
            ResponseTypes.optionalInstanceOf(RestaurantResponse::class.java)
        ).get().isPresent
    }
}
