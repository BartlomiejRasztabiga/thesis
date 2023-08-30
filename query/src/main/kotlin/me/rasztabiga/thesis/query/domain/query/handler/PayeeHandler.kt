package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.exception.UserNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.UserMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.UserMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.repository.PayeeRepository
import me.rasztabiga.thesis.query.domain.query.repository.UserRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
@ProcessingGroup("payee")
class PayeeHandler(
    private val payeeRepository: PayeeRepository
) {

    @EventHandler
    fun on(event: UserCreatedEvent) {
        val entity = mapToEntity(event)
        userRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindPayeeByUserId): Mono<UserResponse> {
        return userRepository.load(query.userId)
            ?.let { Mono.just(mapToResponse(it)) }
            ?: Mono.error(UserNotFoundException(query.userId))
    }
}
