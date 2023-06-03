package me.rasztabiga.thesis.order.config.upcaster

import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Suppress("ClassNaming")
@Component
@Order(1)
class OrderStartedEvent2_to_3Upcaster : SingleEventUpcaster() {

    companion object {
        private val TARGET_TYPE = SimpleSerializedType(
            OrderStartedEvent::class.java.typeName,
            "2.0"
        )
    }

    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == TARGET_TYPE
    }

    override fun doUpcast(
        intermediateRepresentation: IntermediateEventRepresentation
    ): IntermediateEventRepresentation {
        return intermediateRepresentation.upcastPayload(
            SimpleSerializedType(TARGET_TYPE.name, "3.0"),
            Document::class.java
        ) { document: Document ->
            document.rootElement.addElement("restaurantId").text = UUID.randomUUID().toString()
            document.rootElement.addElement("userId").text = UUID.randomUUID().toString()
            document
        }
    }
}
