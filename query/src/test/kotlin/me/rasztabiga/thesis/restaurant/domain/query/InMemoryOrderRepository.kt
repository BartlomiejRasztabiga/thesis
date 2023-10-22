package me.rasztabiga.thesis.restaurant.domain.query

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.query.domain.query.repository.OrderRepository
import me.rasztabiga.thesis.shared.BaseInMemoryRepository
import reactor.core.publisher.Flux
import java.util.*

class InMemoryOrderRepository : OrderRepository,
    BaseInMemoryRepository<OrderEntity>() {

    override fun save(order: OrderEntity) {
        addEntity(order)
    }

    override fun load(id: UUID): OrderEntity? {
        return loadEntity(id)
    }

    override fun loadByUserId(userId: String): Flux<OrderEntity> {
        return Flux.fromIterable(loadAllEntities().filter { it.userId == userId })
    }
}
