package me.rasztabiga.thesis.query.adapter.out.db

import me.rasztabiga.thesis.query.domain.query.entity.PayeeEntity
import me.rasztabiga.thesis.query.domain.query.repository.PayeeRepository
import me.rasztabiga.thesis.query.infrastructure.db.SpringDataPayeeRepository
import org.springframework.stereotype.Service

@Service
class DbPayeeRepository(
    private val springDataPayeeRepository: SpringDataPayeeRepository
) : PayeeRepository {

    override fun save(payee: PayeeEntity) {
        springDataPayeeRepository.save(payee).block()
    }

    override fun loadByUserId(userId: String): PayeeEntity? {
        return springDataPayeeRepository.findByUserId(userId).block()
    }
}
