package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressCreatedEvent
import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressDeletedEvent
import me.rasztabiga.thesis.order.domain.query.entity.UserEntity
import me.rasztabiga.thesis.query.domain.query.exception.UserNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.UserMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.UserMapper.mapToResponse
import me.rasztabiga.thesis.order.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.order.domain.query.repository.UserRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
@ProcessingGroup("user")
class UserHandler(
    private val userRepository: UserRepository
) {

    @EventHandler
    fun on(event: UserCreatedEvent) {
        val entity = mapToEntity(event)
        userRepository.save(entity)
    }

    @EventHandler
    fun on(event: DeliveryAddressCreatedEvent) {
        val entity = userRepository.load(event.userId) ?: throw UserNotFoundException(event.userId)
        entity.deliveryAddresses.add(UserEntity.DeliveryAddress(event.addressId, event.address, event.additionalInfo))
        userRepository.save(entity)
    }

    @EventHandler
    fun on(event: DeliveryAddressDeletedEvent) {
        val entity = userRepository.load(event.userId) ?: throw UserNotFoundException(event.userId)
        entity.deliveryAddresses.removeIf { it.id == event.addressId }
        userRepository.save(entity)
    }

    @Suppress("UnusedParameter")
    @QueryHandler
    fun handle(query: FindAllUsersQuery): Flux<UserResponse> {
        return userRepository.loadAll().map { mapToResponse(it) }
    }

    @QueryHandler
    fun handle(query: FindUserByIdQuery): Mono<UserResponse> {
        return userRepository.load(query.userId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(UserNotFoundException(query.userId))
    }
}
