package me.rasztabiga.thesis.query.domain.query.handler

import me.rasztabiga.thesis.query.domain.query.entity.PayeeEntity
import me.rasztabiga.thesis.query.domain.query.exception.PayeeByUserIdNotFoundException
import me.rasztabiga.thesis.query.domain.query.exception.PayeeNotFoundException
import me.rasztabiga.thesis.query.domain.query.mapper.PayeeMapper.mapToEntity
import me.rasztabiga.thesis.query.domain.query.mapper.PayeeMapper.mapToResponse
import me.rasztabiga.thesis.query.domain.query.repository.PayeeRepository
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.PayeeResponse
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceWithdrawnEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindPayeeByIdQuery
import me.rasztabiga.thesis.shared.domain.query.query.FindPayeeByUserIdQuery
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Component
@ProcessingGroup("projection")
class PayeeHandler(
    private val payeeRepository: PayeeRepository
) {

    @EventHandler
    fun on(event: PayeeCreatedEvent) {
        val entity = mapToEntity(event)
        payeeRepository.save(entity)
    }

    @EventHandler
    fun on(event: PayeeBalanceAddedEvent) {
        val entity = getPayee(event.payeeId)
        entity.balance += event.amount
        entity.balanceChanges.add(
            PayeeEntity.BalanceChange(
                amount = event.amount,
                accountNumber = null,
                timestamp = Instant.now()
            )
        )
        payeeRepository.save(entity)
    }

    @EventHandler
    fun on(event: PayeeBalanceWithdrawnEvent) {
        val entity = getPayee(event.payeeId)
        entity.balance -= event.amount
        entity.balanceChanges.add(
            PayeeEntity.BalanceChange(
                amount = -event.amount,
                accountNumber = event.targetBankAccount,
                timestamp = Instant.now()
            )
        )
        payeeRepository.save(entity)
    }

    @QueryHandler
    fun handle(query: FindPayeeByUserIdQuery): Mono<PayeeResponse> {
        val payee = payeeRepository.loadByUserId(query.userId) ?: throw PayeeByUserIdNotFoundException(query.userId)
        return Mono.just(mapToResponse(payee))
    }

    @QueryHandler
    fun handle(query: FindPayeeByIdQuery): Mono<PayeeResponse> {
        val payee = payeeRepository.load(query.id) ?: throw PayeeNotFoundException(query.id)
        return Mono.just(mapToResponse(payee))
    }

    private fun getPayee(payeeId: UUID): PayeeEntity {
        return payeeRepository.load(payeeId) ?: throw PayeeNotFoundException(payeeId)
    }
}
