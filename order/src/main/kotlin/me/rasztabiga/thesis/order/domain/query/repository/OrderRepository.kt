package me.rasztabiga.thesis.order.domain.query.repository

import me.rasztabiga.thesis.order.domain.query.entity.OrderEntity
import java.util.UUID

interface OrderRepository {

    fun save(order: OrderEntity)

    fun load(id: UUID): OrderEntity?
}
