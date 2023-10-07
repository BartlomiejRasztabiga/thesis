package me.rasztabiga.thesis.order.adapter.out.axon

import me.rasztabiga.thesis.order.domain.command.port.OrderVerificationPort
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*

@Service
class AxonOrderVerificationAdapter(
    private val queryGateway: QueryGateway
) : OrderVerificationPort {
    override fun restaurantExists(restaurantId: UUID): Boolean {
        val restaurant = queryGateway.query(
            FindRestaurantByIdQuery(
                restaurantId,
            ),
            ResponseTypes.optionalInstanceOf(RestaurantResponse::class.java)
        ).get()

        return restaurant.isPresent
    }

    override fun isRestaurantOpen(restaurantId: UUID): Boolean {
        val restaurant = queryGateway.query(
            FindRestaurantByIdQuery(
                restaurantId,
            ),
            ResponseTypes.instanceOf(RestaurantResponse::class.java)
        ).get()

        return restaurant.availability == RestaurantResponse.Availability.OPEN
    }

    override fun userExists(userId: String): Boolean {
        val user = queryGateway.query(
            FindUserByIdQuery(
                userId,
            ),
            ResponseTypes.optionalInstanceOf(UserResponse::class.java)
        ).get()

        return user.isPresent
    }

    override fun productExists(productId: UUID, restaurantId: UUID): Boolean {
        val restaurant = queryGateway.query(
            FindRestaurantByIdQuery(
                restaurantId,
            ),
            ResponseTypes.instanceOf(RestaurantResponse::class.java)
        ).get()

        return restaurant.menu.any { it.id == productId }
    }

    override fun deliveryAddressExists(deliveryAddressId: UUID, userId: String): Boolean {
        val user = queryGateway.query(
            FindUserByIdQuery(
                userId,
            ),
            ResponseTypes.instanceOf(UserResponse::class.java)
        ).get()

        return user.deliveryAddresses.any { it.id == deliveryAddressId }
    }
}
