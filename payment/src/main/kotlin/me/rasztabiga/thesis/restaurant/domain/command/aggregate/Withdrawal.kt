package me.rasztabiga.thesis.restaurant.domain.command.aggregate;

import org.axonframework.modelling.command.EntityId
import java.math.BigDecimal
import java.time.Instant
import java.util.*

internal class Withdrawal {

    @EntityId
    private var id: UUID
    private var timestamp: Instant
    private var amount: BigDecimal
    private var targetBankAccount: String

    constructor(id: UUID, timestamp: Instant, amount: BigDecimal, targetBankAccount: String) {
        this.id = id
        this.timestamp = timestamp
        this.amount = amount
        this.targetBankAccount = targetBankAccount
    }
}
