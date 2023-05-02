package me.rasztabiga.thesis.order.domain.query.handler

import me.rasztabiga.thesis.order.domain.query.entity.UserEntity
import reactor.core.publisher.Flux

interface UserRepository {

    fun save(user: UserEntity)

    fun load(id: String): UserEntity?

    fun loadAll(): Flux<UserEntity>
}
