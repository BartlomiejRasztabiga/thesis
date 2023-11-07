package me.rasztabiga.thesis.restaurant.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.query.domain.query.repository.CourierRepository
import me.rasztabiga.thesis.query.domain.query.repository.OrderRepository
import me.rasztabiga.thesis.shared.BaseInMemoryRepository
import reactor.core.publisher.Flux
import java.util.*

class InMemoryCourierRepository : CourierRepository,
    BaseInMemoryRepository<CourierEntity>() {

    override fun save(courier: CourierEntity) {
        addEntity(courier)
    }

    override fun load(id: String): CourierEntity? {
        return loadEntity(id)
    }

    override fun loadAllOnlineWithoutCurrentDelivery(): List<CourierEntity> {
        return loadAllEntities()
    }
}
