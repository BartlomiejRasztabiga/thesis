@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.query.adapter.`in`.rest

import me.rasztabiga.thesis.query.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByManagerIdQuery
import me.rasztabiga.thesis.shared.security.Scopes.RESTAURANT
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v2/restaurants")
class RestaurantController(
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.READ}')")
    fun getRestaurants(
        exchange: ServerWebExchange
    ): Mono<List<RestaurantResponse>> {
        return reactorQueryGateway.query(
            FindAllRestaurantsQuery(exchange.getUserId()),
            ResponseTypes.multipleInstancesOf(RestaurantResponse::class.java)
        )
    }

    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.READ}')")
    fun getRestaurant(@PathVariable restaurantId: UUID, exchange: ServerWebExchange): Mono<RestaurantResponse> {
        return reactorQueryGateway.query(
            FindRestaurantByIdQuery(restaurantId, exchange.getUserId()),
            ResponseTypes.instanceOf(RestaurantResponse::class.java)
        )
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.READ}')")
    fun getRestaurantForCurrentUser(exchange: ServerWebExchange): Mono<RestaurantResponse> {
        return reactorQueryGateway.query(
            FindRestaurantByManagerIdQuery(exchange.getUserId()),
            ResponseTypes.instanceOf(RestaurantResponse::class.java)
        )
    }
}
