package me.rasztabiga.thesis.payment.adapter.out.rest

import com.fasterxml.jackson.annotation.JsonProperty
import me.rasztabiga.thesis.payment.domain.command.port.InvoiceGenerationPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.util.StringTokenizer

@Profile("invoice")
@Service
class RestInvoiceGenerationAdapter : InvoiceGenerationPort {
    override fun generate(invoiceData: InvoiceGenerationPort.InvoiceData): ByteArray? {
        val webClient = WebClient.builder().build()

        val file = webClient.post()
            .uri("https://invoice-generator.com")
            .bodyValue(
                CreateInvoiceRequest(
                    from = invoiceData.from,
                    to = invoiceData.to,
                    number = invoiceData.id.toString(),
                    currency = "PLN",
                    date = invoiceData.issueDate.toString(),
                    dueDate = invoiceData.dueDate.toString(),
                    items = invoiceData.items.map {
                        CreateInvoiceRequest.InvoiceItem(
                            name = it.name,
                            quantity = it.quantity,
                            unitCost = it.unitPrice
                        )
                    },
                    amountPaid = invoiceData.amountPaid
                )
            )
            .retrieve()
            .bodyToMono(ByteArray::class.java)
            .block()

        return file
    }


    data class CreateInvoiceRequest(
        val from: String,
        val to: String,
        val number: String,
        val currency: String,
        val date: String,
        @get:JsonProperty("due_date") val dueDate: String,
        @get:JsonProperty("amount_paid") val amountPaid: String?,
        val items: List<InvoiceItem>
    ) {
        data class InvoiceItem(
            val name: String,
            val quantity: Int,
            @get:JsonProperty("unit_cost") val unitCost: BigDecimal
        )
    }
}
