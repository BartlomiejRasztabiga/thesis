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

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override fun verifyRestaurantByManagerIdExists(managerId: String): Boolean {
        return try {
            queryGateway.query(
                FindRestaurantByManagerIdQuery(
                    managerId,
                ),
                ResponseTypes.optionalInstanceOf(RestaurantResponse::class.java)
            ).join()
            true
        } catch (e: Exception) {
            false
        }
    }
}
