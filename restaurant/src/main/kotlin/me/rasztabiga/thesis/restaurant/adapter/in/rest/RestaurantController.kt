@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper.RestaurantControllerMapper.mapToCreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper.RestaurantControllerMapper.mapToUpdateRestaurantCommand
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes.RESTAURANT
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantController(
    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.READ}')")
    fun getRestaurants(): Mono<List<RestaurantResponse>> {
        return reactorQueryGateway.query(
            FindAllRestaurantsQuery(),
            ResponseTypes.multipleInstancesOf(RestaurantResponse::class.java)
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.WRITE}')")
    fun createRestaurant(@RequestBody request: CreateRestaurantRequest): Mono<UuidWrapper> {
        val command = mapToCreateRestaurantCommand(request)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.WRITE}')")
    fun updateRestaurant(
        @RequestBody request: UpdateRestaurantRequest,
        @PathVariable restaurantId: UUID
    ): Mono<UuidWrapper> {
        val command = mapToUpdateRestaurantCommand(request, restaurantId)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }
}
