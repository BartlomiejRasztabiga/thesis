package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SpringDataRestaurantRepository : ReactiveMongoRepository<RestaurantEntity, UUID>
