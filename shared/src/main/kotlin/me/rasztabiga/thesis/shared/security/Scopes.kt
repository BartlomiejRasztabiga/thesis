package me.rasztabiga.thesis.shared.security

object Scopes {

    val RESTAURANT = RestaurantScopes

    object RestaurantScopes {
        private const val RESTAURANTS = "restaurants"

        const val READ = "${SCOPE}_read:${RESTAURANTS}"
        const val WRITE = "${SCOPE}_write:${RESTAURANTS}"
    }

    private const val SCOPE = "SCOPE"
}
