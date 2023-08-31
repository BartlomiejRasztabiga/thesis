package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.shared.domain.command.command.AddPayeeBalanceCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.command.StartPayeeWithdrawalCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeWithdrawalStartedEvent
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Aggregate
internal class Payee {
    // either restaurant manager or courier, both have some balance and can withdraw money

    // TODO wszystko z tego jest potrzebne?
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var userId: String
    private lateinit var name: String
    private lateinit var email: String

    private lateinit var balance: BigDecimal

    @AggregateMember
    private val withdrawals: MutableList<Withdrawal> = mutableListOf()

    private constructor()

    @CommandHandler
    constructor(command: CreatePayeeCommand) {
        apply(
            PayeeCreatedEvent(
                payeeId = command.id,
                userId = command.userId,
                name = command.name,
                email = command.email
            )
        )
    }

    @CommandHandler
    fun handle(command: AddPayeeBalanceCommand) {
        apply(
            PayeeBalanceAddedEvent(
                payeeId = command.payeeId,
                amount = command.amount
            )
        )
    }

    @CommandHandler
    fun handle(command: StartPayeeWithdrawalCommand) {
        require(command.userId == this.userId) {
            "Withdrawal can be started only by the payee"
        }

        require(command.amount <= this.balance) {
            "Withdrawal amount cannot be greater than the balance"
        }

        apply(
            PayeeWithdrawalStartedEvent(
                payeeId = command.payeeId,
                amount = command.amount,
                targetBankAccount = command.targetBankAccount
            )
        )
    }

    @EventSourcingHandler
    fun on(event: PayeeCreatedEvent) {
        this.id = event.payeeId
        this.userId = event.userId
        this.name = event.name
        this.email = event.email
        this.balance = BigDecimal.ZERO
    }

    @EventSourcingHandler
    fun on(event: PayeeBalanceAddedEvent) {
        this.balance += event.amount
    }

    @EventSourcingHandler
    fun on(event: PayeeWithdrawalStartedEvent) {
        this.balance -= event.amount
        this.withdrawals.add(
            Withdrawal(
                id = event.payeeId,
                timestamp = Instant.now(),
                amount = event.amount,
                targetBankAccount = event.targetBankAccount
            )
        )
    }
}
