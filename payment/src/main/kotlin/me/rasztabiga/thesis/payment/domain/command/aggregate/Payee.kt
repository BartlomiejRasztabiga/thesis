package me.rasztabiga.thesis.payment.domain.command.aggregate

import me.rasztabiga.thesis.shared.domain.command.command.AddPayeeBalanceCommand
import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import me.rasztabiga.thesis.shared.domain.command.command.WithdrawPayeeBalanceCommand
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceAddedEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceWithdrawnEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
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
    private var withdrawals: MutableList<Withdrawal> = mutableListOf()

    private constructor()

    @CommandHandler
    constructor(command: CreatePayeeCommand) {
        apply(
            PayeeCreatedEvent(
                payeeId = command.id,
                userId = command.userId,
                name = command.name,
                email = command.email,
                payeeType = PayeeCreatedEvent.PayeeType.valueOf(command.payeeType.name)
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
    fun handle(command: WithdrawPayeeBalanceCommand) {
        require(command.userId == this.userId) {
            "Withdrawal can be started only by the payee"
        }

        require(command.amount <= this.balance) {
            "Withdrawal amount cannot be greater than the balance"
        }

        apply(
            PayeeBalanceWithdrawnEvent(
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
        this.withdrawals = mutableListOf()
    }

    @EventSourcingHandler
    fun on(event: PayeeBalanceAddedEvent) {
        this.balance += event.amount
    }

    @EventSourcingHandler
    fun on(event: PayeeBalanceWithdrawnEvent) {
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
