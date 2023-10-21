package me.rasztabiga.thesis.payment.adapter.out.fake

import me.rasztabiga.thesis.payment.domain.command.port.EmailSendingPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service


@Profile("!mailjet")
@Service
class FakeEmailSendingAdapter : EmailSendingPort {
    override fun send(email: String, attachment: ByteArray) {
        println("Sending email to $email with attachment of size ${attachment.size}")
    }
}
