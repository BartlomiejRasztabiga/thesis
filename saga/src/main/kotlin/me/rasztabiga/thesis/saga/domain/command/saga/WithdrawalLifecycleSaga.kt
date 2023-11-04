package me.rasztabiga.thesis.saga.domain.command.saga

import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.PayeeResponse
import me.rasztabiga.thesis.shared.domain.command.command.CreateInvoiceCommand
import me.rasztabiga.thesis.shared.domain.command.command.SendInvoiceEmailCommand
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceCreatedEvent
import me.rasztabiga.thesis.shared.domain.command.event.InvoiceEmailSentEvent
import me.rasztabiga.thesis.shared.domain.command.event.PayeeBalanceWithdrawnEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindPayeeByIdQuery
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.*

@Suppress("TooManyFunctions")
@Saga
@ProcessingGroup("withdrawalsaga")
class WithdrawalLifecycleSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    @Autowired
    @Transient
    private lateinit var queryGateway: QueryGateway

    @Suppress("MagicNumber")
    @StartSaga
    @SagaEventHandler(associationProperty = "payeeId")
    fun on(event: PayeeBalanceWithdrawnEvent) {
        val payee = getPayeeById(event.payeeId)
        val invoiceId = UUID.randomUUID()

        SagaLifecycle.associateWith("invoiceId", invoiceId.toString())

        when (payee.type) {
            PayeeResponse.PayeeType.RESTAURANT_MANAGER -> {
                commandGateway.sendAndWait<Void>(
                    CreateInvoiceCommand(
                        id = invoiceId,
                        from = payee.name,
                        to = "Food Delivery App",
                        issueDate = LocalDate.now(),
                        dueDate = LocalDate.now().plusDays(14),
                        items = listOf(
                            CreateInvoiceCommand.InvoiceItem(
                                name = "Restaurant services",
                                quantity = 1,
                                unitPrice = event.amount
                            )
                        ),
                        amountPaid = null
                    )
                )
            }

            PayeeResponse.PayeeType.COURIER -> {
                commandGateway.sendAndWait<Void>(
                    CreateInvoiceCommand(
                        id = invoiceId,
                        from = payee.name,
                        to = "Food Delivery App",
                        issueDate = LocalDate.now(),
                        dueDate = LocalDate.now().plusDays(14),
                        items = listOf(
                            CreateInvoiceCommand.InvoiceItem(
                                name = "Delivery services",
                                quantity = 1,
                                unitPrice = event.amount
                            )
                        ),
                        amountPaid = null
                    )
                )
            }
        }
    }

    @Suppress("UnusedParameter")
    @SagaEventHandler(associationProperty = "invoiceId")
    fun on(event: InvoiceCreatedEvent) {
        val payee = getPayeeById(event.payeeId)

        commandGateway.sendAndWait<Void>(
            SendInvoiceEmailCommand(
                id = event.invoiceId,
                email = payee.email
            )
        )
    }

    @Suppress("UnusedParameter", "EmptyFunctionBlock")
    @EndSaga
    @SagaEventHandler(associationProperty = "invoiceId")
    fun on(event: InvoiceEmailSentEvent) {

    }

    private fun getPayeeById(id: UUID): PayeeResponse {
        return queryGateway.query(
            FindPayeeByIdQuery(id), ResponseTypes.instanceOf(PayeeResponse::class.java)
        ).join()
    }
}
