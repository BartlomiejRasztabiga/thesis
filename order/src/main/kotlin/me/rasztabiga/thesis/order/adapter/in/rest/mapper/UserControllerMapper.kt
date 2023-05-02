package me.rasztabiga.thesis.order.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.order.domain.command.command.CreateUserCommand

object UserControllerMapper {
    fun mapToCreateUserCommand(request: CreateUserRequest): CreateUserCommand {
        return CreateUserCommand(
            id = request.id,
            name = request.name
        )
    }
}
