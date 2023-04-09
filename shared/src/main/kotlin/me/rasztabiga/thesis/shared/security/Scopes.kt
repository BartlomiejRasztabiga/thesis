package me.rasztabiga.thesis.shared.security

object Scopes {

    val RESTAURANT = RestaurantScopes

    object RestaurantScopes {
        private const val RESTAURANT = "${SCOPE}_restaurant"

        const val READ = "${RESTAURANT}.read"
        const val WRITE = "${RESTAURANT}.write"
    }

    private const val SCOPE = "SCOPE"
}
