package me.rasztabiga.thesis.query.infrastructure.db

import me.rasztabiga.thesis.order.domain.query.entity.UserEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringDataUserRepository : ReactiveMongoRepository<UserEntity, String>
