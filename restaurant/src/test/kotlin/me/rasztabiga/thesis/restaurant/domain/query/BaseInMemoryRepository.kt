package me.rasztabiga.thesis.restaurant.domain.query

import java.util.UUID

open class BaseInMemoryRepository<T> {

    private val entities = mutableMapOf<UUID, T>()

    protected fun addEntity(entity: T) {
        entities[UUID.randomUUID()] = entity
    }

    protected fun loadEntity(id: UUID): T? {
        return entities[id]
    }

    protected fun loadAllEntities(): List<T> {
        return entities.values.toList()
    }
}
