package me.rasztabiga.thesis.delivery.adapter.`in`.rest.mapper

import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.CreateCourierRequest
import me.rasztabiga.thesis.delivery.adapter.`in`.rest.api.UpdateCourierAvailabilityRequest
import me.rasztabiga.thesis.delivery.domain.command.aggregate.Availability
import me.rasztabiga.thesis.delivery.domain.command.command.CreateCourierCommand
import me.rasztabiga.thesis.delivery.domain.command.command.UpdateCourierAvailabilityCommand
import me.rasztabiga.thesis.shared.config.getUserId
import org.springframework.web.server.ServerWebExchange

object CourierControllerMapper {

    fun mapToCreateCourierCommand(
        request: CreateCourierRequest,
        exchange: ServerWebExchange
    ): CreateCourierCommand {
        return CreateCourierCommand(
            id = exchange.getUserId(),
            name = request.name
        )
    }

    fun mapToUpdateCourierAvailabilityCommand(
        request: UpdateCourierAvailabilityRequest,
        exchange: ServerWebExchange
    ): UpdateCourierAvailabilityCommand {
        return UpdateCourierAvailabilityCommand(
            id = exchange.getUserId(),
            availability = Availability.valueOf(request.availability.name)
        )
    }
}
