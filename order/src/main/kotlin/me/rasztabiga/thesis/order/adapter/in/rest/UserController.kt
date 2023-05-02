@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.order.adapter.`in`.rest.mapper.UserControllerMapper.mapToCreateUserCommand
import me.rasztabiga.thesis.order.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.order.domain.query.query.FindUserByIdQuery
import me.rasztabiga.thesis.shared.StringIdWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('${Scopes.USER.WRITE}')")
    fun createUser(@RequestBody request: CreateUserRequest): Mono<StringIdWrapper> {
        val command = mapToCreateUserCommand(request)
        val id = reactorCommandGateway.send<String>(command)
        return id.map { StringIdWrapper(it) }
    }
}
