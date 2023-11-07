package me.rasztabiga.thesis.payment.adapter.out.fake

import me.rasztabiga.thesis.payment.domain.command.port.InvoiceGenerationPort
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service


@Profile("!invoice")
@Service
class FakeInvoiceGenerationAdapter : InvoiceGenerationPort {
    override fun generate(invoiceData: InvoiceGenerationPort.InvoiceData): ByteArray? {
        return "".toByteArray()
    }
}
