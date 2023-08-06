@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateDeliveryAddressRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToCreateDeliveryAddressCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToCreateUserCommand
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToDeleteDeliveryAddressCommand
import me.rasztabiga.thesis.order.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.shared.StringIdWrapper
import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    private val reactorCommandGateway: ReactorCommandGateway,
    private val reactorQueryGateway: ReactorQueryGateway
) {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.READ}')")
    fun getUsers(): Mono<List<UserResponse>> {
        return reactorQueryGateway.query(
            FindAllUsersQuery(),
            ResponseTypes.multipleInstancesOf(UserResponse::class.java)
        )
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.READ}')")
    fun getUser(@PathVariable userId: String): Mono<UserResponse> {
        return reactorQueryGateway.query(
            FindUserByIdQuery(userId),
            ResponseTypes.instanceOf(UserResponse::class.java)
        )
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.READ}')")
    fun getCurrentUser(exchange: ServerWebExchange): Mono<UserResponse> {
        return reactorQueryGateway.query(
            FindUserByIdQuery(exchange.getUserId()),
            ResponseTypes.instanceOf(UserResponse::class.java)
        )
    }

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
}
