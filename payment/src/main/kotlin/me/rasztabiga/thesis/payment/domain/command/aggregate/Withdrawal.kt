package me.rasztabiga.thesis.payment.domain.command.aggregate;

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Withdrawal) return false

        if (id != other.id) return false
        if (amount != other.amount) return false
        if (targetBankAccount != other.targetBankAccount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + targetBankAccount.hashCode()
        return result
    }
}
