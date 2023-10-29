package me.rasztabiga.thesis.restaurant.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.query.domain.query.repository.RestaurantRepository
import me.rasztabiga.thesis.shared.BaseInMemoryRepository
import reactor.core.publisher.Flux
import java.util.*

class InMemoryRestaurantRepository : RestaurantRepository,
    BaseInMemoryRepository<RestaurantEntity>() {

    override fun save(restaurant: RestaurantEntity) {
        addEntity(restaurant)
    }

    override fun load(id: UUID): RestaurantEntity? {
        return loadEntity(id)
    }

    override fun loadByManagerId(id: String): RestaurantEntity? {
        return loadAllEntities().find { it.managerId == id }
    }

    override fun loadAll(): Flux<RestaurantEntity> {
        return Flux.fromIterable(loadAllEntities())
    }

    override fun delete(id: UUID) {
        deleteEntity(id)
    }
}
