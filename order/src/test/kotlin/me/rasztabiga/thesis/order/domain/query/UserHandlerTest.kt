package me.rasztabiga.thesis.order.domain.query

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressCreatedEvent
import me.rasztabiga.thesis.order.domain.command.event.DeliveryAddressDeletedEvent
import me.rasztabiga.thesis.order.domain.query.handler.UserHandler
import me.rasztabiga.thesis.order.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.shared.domain.command.event.UserCreatedEvent
import me.rasztabiga.thesis.shared.domain.query.query.FindUserByIdQuery
import org.junit.jupiter.api.Test
import java.util.*

class UserHandlerTest {

    private val userRepository = InMemoryUserRepository()

    private val userHandler = UserHandler(userRepository)

    @Test
    fun `given user created event, when handling FindAllUsersQuery, then returns users`() {
        // given
        val userCreatedEvent = UserCreatedEvent("1", "User")
        userHandler.on(userCreatedEvent)

        // when
        val users = userHandler.handle(FindAllUsersQuery()).collectList().block()

        // then
        users shouldNotBe null
        users!!.size shouldBe 1
        users[0].id shouldBe userCreatedEvent.userId
        users[0].name shouldBe userCreatedEvent.name
    }

    @Test
    fun `given user created event, when handling FindUserByIdQuery, then returns user`() {
        // given
        val userCreatedEvent = UserCreatedEvent("1", "User")
        userHandler.on(userCreatedEvent)

        // when
        val user = userHandler.handle(FindUserByIdQuery(userCreatedEvent.userId)).block()

        // then
        user shouldNotBe null
        user!!.id shouldBe userCreatedEvent.userId
        user.name shouldBe userCreatedEvent.name
    }

    @Suppress("MaxLineLength")
    @Test
    fun `given delivery address created event, when handling FindUserByIdQuery, then returns user with delivery address`() {
        // given
        val userCreatedEvent = UserCreatedEvent("1", "User")
        userHandler.on(userCreatedEvent)
        val deliveryAddressCreatedEvent = DeliveryAddressCreatedEvent("1", UUID.randomUUID(), "address", "info")
        userHandler.on(deliveryAddressCreatedEvent)

        // when
        val user = userHandler.handle(FindUserByIdQuery(userCreatedEvent.userId)).block()

        // then
        user shouldNotBe null
        user!!.id shouldBe userCreatedEvent.userId
        user.deliveryAddresses.size shouldBe 1
        user.deliveryAddresses[0].id shouldBe deliveryAddressCreatedEvent.addressId
        user.deliveryAddresses[0].address shouldBe deliveryAddressCreatedEvent.address
        user.deliveryAddresses[0].additionalInfo shouldBe deliveryAddressCreatedEvent.additionalInfo
    }

    @Suppress("MaxLineLength")
    @Test
    fun `given delivery address deleted event, when handling FindUserByIdQuery, then returns user without delivery address`() {
        // given
        val userCreatedEvent = UserCreatedEvent("1", "User")
        userHandler.on(userCreatedEvent)
        val deliveryAddressCreatedEvent = DeliveryAddressCreatedEvent("1", UUID.randomUUID(), "address", "info")
        userHandler.on(deliveryAddressCreatedEvent)
        val deliveryAddressDeletedEvent = DeliveryAddressDeletedEvent("1", deliveryAddressCreatedEvent.addressId)
        userHandler.on(deliveryAddressDeletedEvent)

        // when
        val user = userHandler.handle(FindUserByIdQuery(userCreatedEvent.userId)).block()

        // then
        user shouldNotBe null
        user!!.id shouldBe userCreatedEvent.userId
        user.deliveryAddresses.size shouldBe 0
    }
}
