package me.rasztabiga.thesis.query.domain.query.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "payee")
data class PayeeEntity(
    @Id
    val id: String,
    val userId: String,
    val name: String,
    val email: String,
    val balance: BigDecimal
)
