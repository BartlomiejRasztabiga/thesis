package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import java.util.*

interface OrderRepository {

    fun save(order: OrderEntity)

    fun load(id: UUID): OrderEntity?
}
