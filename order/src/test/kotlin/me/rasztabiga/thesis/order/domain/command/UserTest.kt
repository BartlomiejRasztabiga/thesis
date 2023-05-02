package me.rasztabiga.thesis.order.domain.command

import me.rasztabiga.thesis.order.domain.command.aggregate.User
import me.rasztabiga.thesis.order.domain.command.command.CreateUserCommand
import me.rasztabiga.thesis.order.domain.command.event.UserCreatedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserTest {

    private lateinit var textFixture: AggregateTestFixture<User>

    @BeforeEach
    fun setUp() {
        textFixture = AggregateTestFixture(User::class.java)
    }

    @Test
    fun `should create user`() {
        val createUserCommand = CreateUserCommand(
            "1",
            "User"
        )

        val userCreatedEvent = UserCreatedEvent(
            createUserCommand.id,
            createUserCommand.name
        )

        textFixture.givenNoPriorActivity()
            .`when`(createUserCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(userCreatedEvent)
    }
}
