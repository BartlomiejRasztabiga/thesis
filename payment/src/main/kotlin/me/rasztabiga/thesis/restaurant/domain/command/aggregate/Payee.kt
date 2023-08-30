package me.rasztabiga.thesis.restaurant.domain.command.aggregate

import me.rasztabiga.thesis.shared.domain.command.command.CreatePayeeCommand
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
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

    // TODO create

    // TODO withdraw

    // TODO add balance

}
