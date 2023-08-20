package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SpringDataRestaurantRepository : ReactiveMongoRepository<RestaurantEntity, UUID>
