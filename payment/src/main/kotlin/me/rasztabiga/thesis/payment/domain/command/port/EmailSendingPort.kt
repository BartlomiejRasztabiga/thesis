package me.rasztabiga.thesis.payment.domain.command.port

interface EmailSendingPort {

    fun send(email: String, attachment: ByteArray)
}
