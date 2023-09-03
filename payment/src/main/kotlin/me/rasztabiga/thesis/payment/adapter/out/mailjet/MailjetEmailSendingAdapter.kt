package me.rasztabiga.thesis.payment.adapter.out.mailjet

import com.mailjet.client.ClientOptions
import com.mailjet.client.MailjetClient
import com.mailjet.client.transactional.Attachment
import com.mailjet.client.transactional.SendContact
import com.mailjet.client.transactional.SendEmailsRequest
import com.mailjet.client.transactional.TransactionalEmail
import me.rasztabiga.thesis.payment.domain.command.port.EmailSendingPort
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
            .apiKey(System.getenv(mailjetApiKeyPublic))
            .apiSecretKey(System.getenv(mailjetApiKeyPrivate))
            .build()

        val client = MailjetClient(options)

        val message1 = TransactionalEmail
            .builder()
            .to(SendContact(email))
            .from(SendContact("contact@rasztabiga.me"))
            .htmlPart("<h1>This is the HTML content of the mail</h1>")
            .subject("This is the subject")
            .attachment(Attachment.fromInputStream(attachment.inputStream(), "invoice.pdf", "application/pdf"))
            .build()

        val request = SendEmailsRequest
            .builder()
            .message(message1)
            .build()

        request.sendWith(client)
    }
}
