package me.rasztabiga.thesis.shared.security

object Scopes {

    val RESTAURANT = RestaurantScopes

    object RestaurantScopes {
        private const val RESTAURANTS = "restaurants"

        val READ = buildScope("read", RESTAURANTS)
        val WRITE = buildScope("write", RESTAURANTS)
    }

    private const val SCOPE = "SCOPE"

    private fun buildScope(scope: String, resource: String): String {
        return "${SCOPE}_$scope:$resource"
    }
}
