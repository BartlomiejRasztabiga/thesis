package me.rasztabiga.thesis.restaurant.domain.command.port

interface EmailSendingPort {

    fun send(email: String, attachment: ByteArray)
}
