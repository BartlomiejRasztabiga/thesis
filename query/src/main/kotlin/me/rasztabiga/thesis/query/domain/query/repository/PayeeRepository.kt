package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.PayeeEntity

interface PayeeRepository {

    fun save(payee: PayeeEntity)

    fun loadByUserId(userId: String): PayeeEntity?
}
