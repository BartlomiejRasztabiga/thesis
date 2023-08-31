package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.PayeeEntity
import java.util.UUID

interface PayeeRepository {

    fun save(payee: PayeeEntity)

    fun load(id: UUID): PayeeEntity?

    fun loadByUserId(userId: String): PayeeEntity?
}
