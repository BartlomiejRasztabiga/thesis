package me.rasztabiga.thesis.query.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Document(collection = "payee")
data class PayeeEntity(
    @Id
    val id: UUID,
    val userId: String,
    val name: String,
    val email: String,
    var balance: BigDecimal,
    val withdrawals: MutableList<Withdrawal>
) {
    data class Withdrawal(
        val amount: BigDecimal,
        val accountNumber: String,
        val timestamp: Instant
    )
}
