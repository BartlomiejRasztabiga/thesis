package me.rasztabiga.thesis.restaurant.adapter.out.db

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantRepository : MongoRepository<RestaurantEntity, UUID>