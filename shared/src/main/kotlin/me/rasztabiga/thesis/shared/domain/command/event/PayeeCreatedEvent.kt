package me.rasztabiga.thesis.shared.domain.command.event

import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class PayeeCreatedEvent(
    val payeeId: UUID,
    val userId: String,
    val name: String,
    val email: String,
    val payeeType: PayeeType
) {
    enum class PayeeType {
        RESTAURANT_MANAGER, COURIER
    }
}
