@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.delivery.adapter.`in`.rest

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.CourierControllerMapper.mapToCreateCourierCommand
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.CourierControllerMapper.mapToUpdateCourierAvailabilityCommand
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper.CourierControllerMapper.mapToUpdateCourierLocationCommand
import me.rasztabiga.thesis.shared.StringIdWrapper
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CreateCourierRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateCourierAvailabilityRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateCourierLocationRequest
import me.rasztabiga.thesis.shared.security.Scopes.COURIER
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/couriers")
class CourierController(
    private val reactorCommandGateway: ReactorCommandGateway
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${COURIER.WRITE}')")
    fun createCourier(
        @RequestBody request: CreateCourierRequest,
        exchange: ServerWebExchange
    ): Mono<StringIdWrapper> {
        val command = mapToCreateCourierCommand(request, exchange)
        val id = reactorCommandGateway.send<String>(command)
        return id.map { StringIdWrapper(it) }
    }

    @PutMapping("/me/availability")
    @PreAuthorize("hasAnyAuthority('${COURIER.WRITE}')")
    fun updateCourierAvailability(
        @RequestBody request: UpdateCourierAvailabilityRequest,
        exchange: ServerWebExchange
    ): Mono<StringIdWrapper> {
        val command = mapToUpdateCourierAvailabilityCommand(request, exchange)
        val id = reactorCommandGateway.send<String>(command)
        return id.map { StringIdWrapper(it) }
    }

    @PutMapping("/me/location")
    @PreAuthorize("hasAnyAuthority('${COURIER.WRITE}')")
    fun updateCourierLocation(
        @RequestBody request: UpdateCourierLocationRequest,
        exchange: ServerWebExchange
    ): Mono<StringIdWrapper> {
        val command = mapToUpdateCourierLocationCommand(request, exchange)
        val id = reactorCommandGateway.send<String>(command)
        return id.map { StringIdWrapper(it) }
    }
}
