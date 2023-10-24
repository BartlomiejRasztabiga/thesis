package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.PayeeEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.PayeeResponse
import me.rasztabiga.thesis.shared.domain.command.event.PayeeCreatedEvent
import java.math.BigDecimal

object PayeeMapper {

    fun mapToEntity(event: PayeeCreatedEvent): PayeeEntity {
        return PayeeEntity(
            id = event.payeeId,
            userId = event.userId,
            name = event.name,
            email = event.email,
            balance = BigDecimal.ZERO,
            balanceChanges = mutableListOf()
        )
    }

    fun mapToResponse(entity: PayeeEntity): PayeeResponse {
        return PayeeResponse(
            id = entity.id,
            userId = entity.userId,
            name = entity.name,
            email = entity.email,
            balance = entity.balance,
            balanceChanges = entity.balanceChanges.map {
                PayeeResponse.BalanceChange(
                    amount = it.amount,
                    accountNumber = it.accountNumber,
                    timestamp = it.timestamp
                )
            }
        )
    }
}
