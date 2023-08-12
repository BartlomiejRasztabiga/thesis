package me.rasztabiga.thesis.restaurant.infrastructure.db

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantOrderEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*

@Repository
interface SpringDataRestaurantOrderRepository : ReactiveMongoRepository<RestaurantOrderEntity, UUID> {

        fun findAllByRestaurantId(restaurantId: UUID): Flux<RestaurantOrderEntity>
}
