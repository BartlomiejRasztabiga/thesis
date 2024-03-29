@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.query.adapter.`in`.rest

import me.rasztabiga.thesis.query.domain.query.query.FindAllRestaurantOrdersByRestaurantQuery
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantOrderResponse
import me.rasztabiga.thesis.shared.security.Scopes.RESTAURANT
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v2/restaurants/{restaurantId}/orders")
class RestaurantOrderController(
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.READ}')")
    fun getOrders(@PathVariable restaurantId: UUID): Mono<List<RestaurantOrderResponse>> {
        return reactorQueryGateway.query(
            FindAllRestaurantOrdersByRestaurantQuery(restaurantId),
            ResponseTypes.multipleInstancesOf(RestaurantOrderResponse::class.java)
        )
    }
}
