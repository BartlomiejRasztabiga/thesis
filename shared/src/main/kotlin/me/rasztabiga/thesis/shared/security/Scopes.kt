package me.rasztabiga.thesis.shared.security

object Scopes {

    val RESTAURANT = RestaurantScopes
    val USER = UserScopes
    val ORDER = OrderScopes

    object RestaurantScopes {
        private const val RESTAURANTS = "restaurants"

        const val READ = "${SCOPE}_read:${RESTAURANTS}"
        const val WRITE = "${SCOPE}_write:${RESTAURANTS}"
    }

    object UserScopes {
        private const val USERS = "users"

        const val READ = "${SCOPE}_read:${USERS}"
        const val WRITE = "${SCOPE}_write:${USERS}"
    }

    object OrderScopes {
        private const val ORDERS = "orders"

        const val READ = "${SCOPE}_read:${ORDERS}"
        const val WRITE = "${SCOPE}_write:${ORDERS}"
    }

    private const val SCOPE = "SCOPE"
}
