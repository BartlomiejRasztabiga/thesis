@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.query.adapter.`in`.rest

import me.rasztabiga.thesis.query.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.config.getUserId
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/users")
class UserController(
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
}
