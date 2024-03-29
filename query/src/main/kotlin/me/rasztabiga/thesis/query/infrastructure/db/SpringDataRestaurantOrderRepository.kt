package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantOrderEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface SpringDataRestaurantOrderRepository : ReactiveMongoRepository<RestaurantOrderEntity, UUID> {

    fun findAllByRestaurantId(restaurantId: UUID): Flux<RestaurantOrderEntity>

    fun findByOrderId(orderId: UUID): Mono<RestaurantOrderEntity>
}
