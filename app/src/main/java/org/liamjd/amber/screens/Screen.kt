package org.liamjd.amber.screens

/**
 * Represents all the possible navigation screens
 * @param route unique name for the screen, for building navigation routes
 */
sealed class Screen(val route: String) {
    object StartScreen : Screen("start")
    object RecordChargingScreen : Screen("record_charge")
    object RecordJourneyScreen : Screen("record_journey")

    /**
     * Build a navigation route, appending each argument in the form /route/{arg1}.../{argN}
     * @param args String arguments to pass to the route
     */
    fun buildRoute(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
