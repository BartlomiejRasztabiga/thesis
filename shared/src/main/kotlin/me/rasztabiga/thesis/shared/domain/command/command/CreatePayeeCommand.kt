package me.rasztabiga.thesis.shared.domain.command.command

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class CreatePayeeCommand(
    @TargetAggregateIdentifier val id: UUID,
    val userId: String,
    val name: String,
    val email: String,
    val payeeType: PayeeType
) {
    enum class PayeeType {
        RESTAURANT_MANAGER, COURIER
    }
}
