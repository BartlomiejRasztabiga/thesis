package me.rasztabiga.thesis.e2e

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.specification.RequestSpecification
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CreateDeliveryAddressRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CreateRestaurantRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.CreateUserRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantAvailability
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateRestaurantAvailabilityRequest
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.UpdateRestaurantMenuRequest
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class E2ETest {

    private lateinit var restaurantManagerRequestSpecification: RequestSpecification
    private lateinit var orderingUserRequestSpecification: RequestSpecification
    private lateinit var restaurantId: UUID
    private lateinit var userId: String

    val log = LoggerFactory.getLogger(E2ETest::class.java)

    @BeforeEach
    fun setUp() {
        val baseUri = "https://thesis.rasztabiga.me/api/v1"

        restaurantManagerRequestSpecification = RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setAuth(RestAssured.oauth2(getRestaurantManagerToken()))
            .build()

        orderingUserRequestSpecification = RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setAuth(RestAssured.oauth2(getOrderingUserToken()))
            .build()

        val responseSpecification = ResponseSpecBuilder()
            .expectResponseTime(lessThan(2L), TimeUnit.SECONDS)
            .build()

        RestAssured.responseSpecification = responseSpecification
    }

    @Test
    fun e2e() {
        setupRestaurant()
        setupOrderingUser()
    }

    private fun setupRestaurant() {
        createOrUseExistingRestaurant()
        updateRestaurantMenu()
        updateRestaurantAvailability()
    }

    private fun setupOrderingUser() {
        createOrUseExistingUser()
    }

    private fun createOrUseExistingRestaurant() {
        // try to get restaurant for current user
        val restaurantResponse = given(restaurantManagerRequestSpecification)
            .`when`()
            .get("/restaurants/me")

        if (restaurantResponse.statusCode == 200) {
            restaurantId = restaurantResponse
                .then()
                .extract()
                .path<String>("id").let { UUID.fromString(it) }

            log.info("Restaurant already exists: $restaurantId")
        } else {
            // create restaurant
            val createRestaurantRequest = CreateRestaurantRequest(
                id = UUID.randomUUID(),
                name = "Restaurant",
                address = "Cypryjska 70, 02-762 Warszawa",
                email = "bartlomiej.rasztabiga.official+restaurant@gmail.com",
                imageUrl = "https://mui.com/static/images/cards/contemplative-reptile.jpg"
            )

            restaurantId = given(restaurantManagerRequestSpecification)
                .contentType("application/json")
                .body(createRestaurantRequest)
                .`when`()
                .post("/restaurants")
                .then()
                .statusCode(201)
                .extract()
                .path<String>("id").let { UUID.fromString(it) }

            log.info("Restaurant created: $restaurantId")
        }
    }

    private fun updateRestaurantMenu() {
        val request = UpdateRestaurantMenuRequest(
            menu = listOf(
                UpdateRestaurantMenuRequest.Product(
                    name = "Burger",
                    description = "with extra cheese",
                    price = 21.37.toBigDecimal(),
                    imageUrl = "https://www.foodiesfeed.com/wp-content/uploads/2023/04/cheeseburger.jpg"
                ),
                UpdateRestaurantMenuRequest.Product(
                    name = "Milkshake",
                    description = "with extra strawberries",
                    price = 23.37.toBigDecimal(),
                    imageUrl = "https://www.foodiesfeed.com/wp-content/uploads/2023/04/strawberry-milk-splash.jpg"
                )
            )
        )

        given(restaurantManagerRequestSpecification)
            .contentType("application/json")
            .body(request)
            .`when`()
            .put("/restaurants/$restaurantId/menu")
            .then()
            .statusCode(200)
    }

    private fun updateRestaurantAvailability() {
        val request = UpdateRestaurantAvailabilityRequest(
            availability = RestaurantAvailability.OPEN
        )

        given(restaurantManagerRequestSpecification)
            .contentType("application/json")
            .body(request)
            .`when`()
            .put("/restaurants/$restaurantId/availability")
            .then()
            .statusCode(200)
    }

    private fun createOrUseExistingUser() {
        // try to get current user
        val userResponse = given(orderingUserRequestSpecification)
            .`when`()
            .get("/users/me")

        if (userResponse.statusCode == 200) {
            userId = userResponse
                .then()
                .extract()
                .path("id")

            if (userResponse.then().extract().path<List<Any>>("deliveryAddresses").isEmpty()) {
                createDeliveryAddress()
            }

            log.info("User already exists: $userId")
        } else {
            val request = CreateUserRequest(
                name = "User",
                email = "bartlomiej.rasztabiga.official+user@gmail.com"
            )

            userId = given(orderingUserRequestSpecification)
                .contentType("application/json")
                .body(request)
                .`when`()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .path("id")

            createDeliveryAddress()

            log.info("User created: $userId")
        }
    }

    private fun createDeliveryAddress() {
        val request = CreateDeliveryAddressRequest(
            address = "Bukowińska 26C, 02-703 Warszawa",
            additionalInfo = null
        )

        given(orderingUserRequestSpecification)
            .contentType("application/json")
            .body(request)
            .`when`()
            .post("/users/$userId/addresses")
            .then()
            .statusCode(201)

        log.info("Delivery address created")
    }

    private fun getRestaurantManagerToken(): String {
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
            .path("access_token")
    }

    private fun getOrderingUserToken(): String {
        return given()
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .accept("application/json")
            .formParam("grant_type", "password")
            .formParam("audience", "https://thesis.rasztabiga.me/api")
            .formParam("username", "user@thesis.rasztabiga.me")
            .formParam("password", "qabdyq-kepTed-xikke2")
            .formParam("client_id", "BVxjiTTdPPXMpUj8gm1KoGgkkQ1fObF4")
            .formParam("client_secret", "gqzR7iyZF5N-l9RTRP9CYCds57AhuHewOsQ3VdPyXxovMPvI61F3-DdXw_qNbNhu")
            .formParam("scope", "read:users write:users read:orders write:orders")
            .`when`()
            .post("https://rasztabigab.eu.auth0.com/oauth/token")
            .then()
            .statusCode(200)
            .and()
            .extract()
            .path("access_token")
    }
}
