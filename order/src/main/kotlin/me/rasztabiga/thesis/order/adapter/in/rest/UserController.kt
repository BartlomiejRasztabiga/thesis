@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateDeliveryAddressRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.UpdateDefaultDeliveryAddressRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToCreateDeliveryAddressCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToCreateUserCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToDeleteDeliveryAddressCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToUpdateDefaultDeliveryAddressCommand
import me.rasztabiga.thesis.shared.StringIdWrapper
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val reactorCommandGateway: ReactorCommandGateway
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun createUser(
        @RequestBody request: CreateUserRequest,
        exchange: ServerWebExchange
    ): Mono<StringIdWrapper> {
        val command = mapToCreateUserCommand(request, exchange)
        val id = reactorCommandGateway.send<String>(command)
        return id.map { StringIdWrapper(it) }
    }

    @PostMapping("/{userId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun createDeliveryAddress(
        @RequestBody request: CreateDeliveryAddressRequest,
        @PathVariable userId: String
    ): Mono<UuidWrapper> {
        val command = mapToCreateDeliveryAddressCommand(request, userId)
        return reactorCommandGateway.send<UUID>(command)
            .then(Mono.just(UuidWrapper(command.addressId)))
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun deleteDeliveryAddress(
        @PathVariable userId: String,
        @PathVariable addressId: UUID
    ): Mono<Void> {
        val command = mapToDeleteDeliveryAddressCommand(userId, addressId)
        return reactorCommandGateway.send(command)
    }

    @PutMapping("/me/default-address")
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun createDeliveryAddress(
        @RequestBody request: UpdateDefaultDeliveryAddressRequest,
        exchange: ServerWebExchange
    ): Mono<Void> {
        val command = mapToUpdateDefaultDeliveryAddressCommand(request, exchange)
        return reactorCommandGateway.send(command)
    }
}
