package me.rasztabiga.thesis.order.config.upcaster

import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Suppress("ClassNaming", "MagicNumber")
@Component
@Order(3)
class UserCreatedEvent1_to_2Upcaster : SingleEventUpcaster() {

    companion object {
        private val TARGET_TYPE = SimpleSerializedType(
            UserCreatedEvent::class.java.typeName,
            "1.0"
        )
    }

    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == TARGET_TYPE
    }

    override fun doUpcast(
        intermediateRepresentation: IntermediateEventRepresentation
    ): IntermediateEventRepresentation {
        return intermediateRepresentation.upcastPayload(
            SimpleSerializedType(TARGET_TYPE.name, "2.0"),
            Document::class.java
        ) { document: Document ->
            document.rootElement.addElement("userId").text = document.rootElement.element("id").text
            document
        }
    }
}
