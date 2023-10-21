package me.rasztabiga.thesis.restaurant.adapter.out.mailjet

import com.mailjet.client.ClientOptions
import com.mailjet.client.MailjetClient
import com.mailjet.client.transactional.Attachment
import com.mailjet.client.transactional.SendContact
import com.mailjet.client.transactional.SendEmailsRequest
import com.mailjet.client.transactional.TransactionalEmail
import me.rasztabiga.thesis.restaurant.domain.command.port.EmailSendingPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service


@Profile("mailjet")
@Service
class MailjetEmailSendingAdapter(
    @Value("\${mailjet.api.key.public}")
    private val mailjetApiKeyPublic: String,
    @Value("\${mailjet.api.key.private}")
    private val mailjetApiKeyPrivate: String
) : EmailSendingPort {
    override fun send(email: String, attachment: ByteArray) {

        val options = ClientOptions.builder()
            .apiKey(mailjetApiKeyPublic)
            .apiSecretKey(mailjetApiKeyPrivate)
            .build()

        val client = MailjetClient(options)

        val message1 = TransactionalEmail
            .builder()
            .to(SendContact(email))
            .from(SendContact("contact@rasztabiga.me"))
            .htmlPart("<h1>In the attachment you will find your invoice</h1>")
            .subject("Food Delivery App - Invoice")
            .attachment(Attachment.fromInputStream(attachment.inputStream(), "invoice.pdf", "application/pdf"))
            .build()

        val request = SendEmailsRequest
            .builder()
            .message(message1)
            .build()

        request.sendWith(client)
    }
}
