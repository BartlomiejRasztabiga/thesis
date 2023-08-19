package me.rasztabiga.thesis.delivery.domain.command.port

import java.util.*

interface OrderPreparedVerifierPort {

    fun isOrderPrepared(orderId: UUID): Boolean
}
