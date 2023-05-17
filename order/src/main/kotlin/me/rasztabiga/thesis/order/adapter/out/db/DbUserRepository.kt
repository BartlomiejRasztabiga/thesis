package me.rasztabiga.thesis.order.adapter.out.db

import me.rasztabiga.thesis.order.domain.query.entity.UserEntity
import me.rasztabiga.thesis.order.domain.query.handler.UserRepository
import me.rasztabiga.thesis.order.infrastructure.db.SpringDataUserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class DbUserRepository(
    private val springDataUserRepository: SpringDataUserRepository
) : UserRepository {

    override fun save(user: UserEntity) {
        springDataUserRepository.save(user).block()
    }

    override fun load(id: String): UserEntity? {
        return springDataUserRepository.findById(id).block()
    }

    override fun loadAll(): Flux<UserEntity> {
        return springDataUserRepository.findAll()
    }
}
