package me.rasztabiga.thesis.order.domain.query

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.rasztabiga.thesis.order.domain.command.event.UserCreatedEvent
import me.rasztabiga.thesis.order.domain.query.handler.UserHandler
import me.rasztabiga.thesis.order.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.order.domain.query.query.FindUserByIdQuery
import org.junit.jupiter.api.Test

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
        users[0].id shouldBe userCreatedEvent.id
        users[0].name shouldBe userCreatedEvent.name
    }

    @Test
    fun `given user created event, when handling FindUserByIdQuery, then returns user`() {
        // given
        val userCreatedEvent = UserCreatedEvent("1", "User")
        userHandler.on(userCreatedEvent)

        // when
        val user = userHandler.handle(FindUserByIdQuery(userCreatedEvent.id)).block()

        // then
        user shouldNotBe null
        user!!.id shouldBe userCreatedEvent.id
        user.name shouldBe userCreatedEvent.name
    }
}
