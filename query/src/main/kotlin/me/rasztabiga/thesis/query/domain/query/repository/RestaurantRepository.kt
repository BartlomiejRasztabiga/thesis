package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantEntity
import reactor.core.publisher.Flux
import java.util.*

interface RestaurantRepository {

    fun save(restaurant: RestaurantEntity)

    fun load(id: UUID): RestaurantEntity?

    fun loadAll(): Flux<RestaurantEntity>

    fun delete(id: UUID)
}
