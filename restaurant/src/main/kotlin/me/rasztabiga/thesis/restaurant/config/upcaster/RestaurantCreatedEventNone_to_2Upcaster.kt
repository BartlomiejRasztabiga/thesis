package me.rasztabiga.thesis.restaurant.config.upcaster

import me.rasztabiga.thesis.restaurant.domain.command.event.RestaurantCreatedEvent
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(0)
class RestaurantCreatedEventNone_to_2Upcaster : SingleEventUpcaster() {

    companion object {
        private val TARGET_TYPE = SimpleSerializedType(
            RestaurantCreatedEvent::class.java.typeName,
            null
        )
    }

    override fun canUpcast(intermediateRepresentation: IntermediateEventRepresentation): Boolean {
        return intermediateRepresentation.type == TARGET_TYPE
    }

    override fun doUpcast(intermediateRepresentation: IntermediateEventRepresentation): IntermediateEventRepresentation {
        return intermediateRepresentation.upcastPayload(
            SimpleSerializedType(TARGET_TYPE.name, "2.0"),
            Document::class.java
        ) { document: Document ->
            document.rootElement.addElement("availability").text = "CLOSED"
            document
        }
    }
}
