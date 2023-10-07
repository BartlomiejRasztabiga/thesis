package me.rasztabiga.thesis.delivery.domain.command.port

interface CourierOnlineVerifierPort {

    fun isCourierOnline(courierId: String): Boolean
}
