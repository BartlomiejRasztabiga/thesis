package me.rasztabiga.thesis.restaurant.adapter.out.db

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantRepository : ReactiveMongoRepository<RestaurantEntity, UUID>
