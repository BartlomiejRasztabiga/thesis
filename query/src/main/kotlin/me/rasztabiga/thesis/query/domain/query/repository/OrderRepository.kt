package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import reactor.core.publisher.Flux
import java.util.*

interface OrderRepository {

    fun save(order: OrderEntity)

    fun load(id: UUID): OrderEntity?

    fun loadByUserId(userId: String): Flux<OrderEntity>
}
