@file:Suppress("InvalidPackageDeclaration")

package me.rasztabiga.thesis.order.adapter.`in`.rest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import me.rasztabiga.thesis.order.BaseWebFluxTest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.order.adapter.`in`.rest.api.UserResponse
import me.rasztabiga.thesis.order.domain.query.query.FindAllUsersQuery
import me.rasztabiga.thesis.order.domain.query.query.FindUserByIdQuery
import me.rasztabiga.thesis.shared.StringIdWrapper
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import reactor.core.publisher.Mono

class UserControllerTest : BaseWebFluxTest() {

    @Test
    fun `when POST is performed on users endpoint, then returns 201 CREATED`() {
        // given
        val request = CreateUserRequest("1", "User")
        every { reactorCommandGateway.send<String>(any()) } returns Mono.just(request.id)

        // when
        val response = webTestClient.post()
            .uri("/api/v1/users")
            .body(Mono.just(request), CreateUserRequest::class.java)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isCreated
            .expectBody(StringIdWrapper::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldBe request.id
    }

    @Test
    fun `when GET is performed on users endpoint, then returns 200 OK`() {
        // given
        val existingUser = UserResponse("1", "User")
        every {
            reactorQueryGateway.query(
                any<FindAllUsersQuery>(),
                ResponseTypes.multipleInstancesOf(UserResponse::class.java)
            )
        } returns Mono.just(listOf(existingUser))

        // when
        val response = webTestClient.get()
            .uri("/api/v1/users")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(UserResponse::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.size shouldBe 1
        response[0].id shouldBe existingUser.id
        response[0].name shouldBe existingUser.name
    }

    @Test
    fun `when GET is performed on user endpoint, then returns 200 OK`() {
        // given
        val existingUser = UserResponse("1", "User")
        every {
            reactorQueryGateway.query(
                any<FindUserByIdQuery>(),
                ResponseTypes.instanceOf(UserResponse::class.java)
            )
        } returns Mono.just(existingUser)

        // when
        val response = webTestClient.get()
            .uri("/api/v1/users/${existingUser.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(UserResponse::class.java)
            .returnResult()
            .responseBody

        // then
        response shouldNotBe null
        response!!.id shouldBe existingUser.id
        response.name shouldBe existingUser.name
    }
}
