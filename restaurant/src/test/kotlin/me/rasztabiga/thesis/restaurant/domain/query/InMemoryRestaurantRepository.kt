package me.rasztabiga.thesis.restaurant.domain.query

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import me.rasztabiga.thesis.restaurant.domain.query.handler.RestaurantRepository
import reactor.core.publisher.Flux

class InMemoryRestaurantRepository : RestaurantRepository,
    BaseInMemoryRepository<RestaurantEntity>() {

    override fun add(restaurant: RestaurantEntity) {
        addEntity(restaurant)
    }

    override fun loadAll(): Flux<RestaurantEntity> {
        return Flux.fromIterable(loadAllEntities())
    }
}
