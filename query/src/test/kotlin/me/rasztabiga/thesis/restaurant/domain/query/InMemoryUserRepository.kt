package me.rasztabiga.thesis.restaurant.domain.query

import me.rasztabiga.thesis.query.domain.query.entity.UserEntity
import me.rasztabiga.thesis.query.domain.query.repository.UserRepository
import me.rasztabiga.thesis.shared.BaseInMemoryRepository
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
