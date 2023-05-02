package me.rasztabiga.thesis.order.domain.query

import me.rasztabiga.thesis.order.domain.query.entity.UserEntity
import me.rasztabiga.thesis.order.domain.query.handler.UserRepository
import reactor.core.publisher.Flux

class InMemoryUserRepository : UserRepository,
    BaseInMemoryRepository<UserEntity>() {

    override fun save(user: UserEntity) {
        addEntity(user)
    }

    override fun load(id: String): UserEntity? {
        return loadEntity(id)
    }

    override fun loadAll(): Flux<UserEntity> {
        return Flux.fromIterable(loadAllEntities())
    }
}
