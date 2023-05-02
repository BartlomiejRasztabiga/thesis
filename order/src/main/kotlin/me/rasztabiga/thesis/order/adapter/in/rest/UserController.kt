@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import me.rasztabiga.thesis.shared.UuidWrapper
import me.rasztabiga.thesis.shared.security.Scopes
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

//@RestController
//@RequestMapping("/api/v1/users")
//class UserController(
//    private val reactorCommandGateway: ReactorCommandGateway,
//    private val reactorQueryGateway: ReactorQueryGateway
//) {
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasAnyAuthority('${Scopes.RESTAURANT.WRITE}')")
//    fun createUser(@RequestBody request: CreateRestaurantRequest): Mono<UuidWrapper> {
//        val command = mapToCreateRestaurantCommand(request)
//        val id = reactorCommandGateway.send<UUID>(command)
//        return id.map { UuidWrapper(it) }
//    }
//}
