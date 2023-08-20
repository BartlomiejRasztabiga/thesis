package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.UserEntity
import me.rasztabiga.thesis.query.domain.query.repository.UserRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataUserRepository
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
