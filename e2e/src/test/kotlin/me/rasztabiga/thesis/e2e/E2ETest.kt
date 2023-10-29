package me.rasztabiga.thesis.e2e

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.TimeUnit

class E2ETest {

    private lateinit var restaurantManagerRequestSpecification: RequestSpecification
    private lateinit var restaurantId: UUID

    @BeforeEach
    fun setUp() {
        restaurantManagerRequestSpecification = RequestSpecBuilder()
//            .setBaseUri("https://thesis.rasztabiga.me/api/v1")
            .setBaseUri("http://localhost:8100/api/v1")
            .setAuth(RestAssured.oauth2(getRestaurantManagerToken()))
            .build()

        val responseSpecification = ResponseSpecBuilder()
            .expectResponseTime(lessThan(1L), TimeUnit.SECONDS)
            .build()

        RestAssured.responseSpecification = responseSpecification
    }

    @Test
    fun e2e() {
        println("Hello, world!")
        createOrUseExistingRestaurant()
    }

    private fun createOrUseExistingRestaurant() {
        RestAssured.requestSpecification = restaurantManagerRequestSpecification

        // try to get restaurant for current user
        val restaurantResponse = given()
            .`when`()
            .get("/restaurants/me")

        if (restaurantResponse.statusCode == 200) {
            restaurantId = restaurantResponse
                .then()
                .extract()
                .path("id")
        } else {
            // create restaurant
            val createRestaurantRequest = CreateRestaurantRequest()

            restaurantId = given()
                .contentType("application/json")
                .body(
                    """
                    {
                        "name": "Restaurant",
                        "address": "Address",
                        "city": "City",
                        "country": "Country",
                        "postalCode": "PostalCode",
                        "phoneNumber": "PhoneNumber"
                    }
                    """.trimIndent()
                )
                .`when`()
                .post("/restaurants")
                .then()
                .statusCode(201)
                .extract()
                .path("id")
        }
    }

    private fun getRestaurantManagerToken(): String? {
        return given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .accept("application/json")
            .formParam("grant_type", "password")
            .formParam("audience", "https://thesis.rasztabiga.me/api")
            .formParam("username", "restaurant@thesis.rasztabiga.me")
            .formParam("password", "sazni3-docmaj-foNnuz")
            .formParam("client_id", "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4")
            .formParam("client_secret", "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu")
            .formParam("scope", "read:restaurants write:restaurants read:users write:users")
            .`when`()
            .post("https://rasztabigab.eu.auth0.com/oauth/token")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .path<String>("access_token")
    }
}
