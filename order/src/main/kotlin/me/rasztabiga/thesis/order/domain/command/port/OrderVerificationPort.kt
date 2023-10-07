package me.rasztabiga.thesis.order.domain.command.port

import java.util.*

interface OrderVerificationPort {

    fun restaurantExists(restaurantId: UUID): Boolean

    fun isRestaurantOpen(restaurantId: UUID): Boolean

    fun userExists(userId: String): Boolean

    fun productExists(productId: UUID, restaurantId: UUID): Boolean

    fun deliveryAddressExists(deliveryAddressId: UUID, userId: String): Boolean
}
