package me.rasztabiga.thesis.query.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Document(collection = "payee")
data class PayeeEntity(
    @Id
    val id: UUID,
    @field:Indexed val userId: String,
    val name: String,
    val email: String,
    var balance: BigDecimal,
    val balanceChanges: MutableList<BalanceChange>,
    val type: PayeeType
) {
    data class BalanceChange(
        val amount: BigDecimal,
        val accountNumber: String?,
        val timestamp: Instant
    )

    enum class PayeeType {
        RESTAURANT_MANAGER, COURIER
    }
}
