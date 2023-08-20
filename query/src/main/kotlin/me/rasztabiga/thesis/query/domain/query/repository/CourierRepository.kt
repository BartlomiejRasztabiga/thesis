package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.delivery.domain.query.entity.CourierEntity

interface CourierRepository {
    fun save(courier: CourierEntity)

    fun load(id: String): CourierEntity?
}
