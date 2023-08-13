@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.restaurant.adapter.`in`.rest

import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantAvailabilityRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantMenuRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.api.UpdateRestaurantRequest
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper.RestaurantControllerMapper.mapToCreateRestaurantCommand
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper.RestaurantControllerMapper.mapToDeleteRestaurantCommand
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper.RestaurantControllerMapper.mapToUpdateRestaurantAvailabilityCommand
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper.RestaurantControllerMapper.mapToUpdateRestaurantCommand
import me.rasztabiga.thesis.restaurant.adapter.`in`.rest.mapper.RestaurantControllerMapper.mapToUpdateRestaurantMenuCommand
import me.rasztabiga.thesis.restaurant.domain.query.query.FindAllRestaurantsQuery
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantResponse
import me.rasztabiga.thesis.shared.domain.query.query.FindRestaurantByIdQuery
import me.rasztabiga.thesis.shared.security.Scopes.RESTAURANT
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
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

    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.READ}')")
    fun getRestaurant(@PathVariable restaurantId: UUID): Mono<RestaurantResponse> {
        return reactorQueryGateway.query(
            FindRestaurantByIdQuery(restaurantId),
            ResponseTypes.instanceOf(RestaurantResponse::class.java)
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

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.WRITE}')")
    fun deleteRestaurant(@PathVariable restaurantId: UUID): Mono<UuidWrapper> {
        val command = mapToDeleteRestaurantCommand(restaurantId)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PutMapping("/{restaurantId}/availability")
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.WRITE}')")
    fun updateRestaurantAvailability(
        @RequestBody request: UpdateRestaurantAvailabilityRequest,
        @PathVariable restaurantId: UUID
    ): Mono<UuidWrapper> {
        val command = mapToUpdateRestaurantAvailabilityCommand(request, restaurantId)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }

    @PutMapping("/{restaurantId}/menu")
    @PreAuthorize("hasAnyAuthority('${RESTAURANT.WRITE}')")
    fun updateRestaurantMenu(
        @RequestBody request: UpdateRestaurantMenuRequest,
        @PathVariable restaurantId: UUID
    ): Mono<UuidWrapper> {
        val command = mapToUpdateRestaurantMenuCommand(request, restaurantId)
        val id = reactorCommandGateway.send<UUID>(command)
        return id.map { UuidWrapper(it) }
    }
}
