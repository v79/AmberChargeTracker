package org.liamjd.amber.screens

/**
 * Represents all the possible navigation screens
 * @param route unique name for the screen, for building navigation routes
 */
sealed class Screen(val route: String) {
    object StartScreen : Screen("start")
    object StartChargingScreen : Screen("start_charge")
    object RecordChargingScreen : Screen("record_charge")
    object RecordJourneyScreen : Screen("record_journey")
    object ChargeHistoryScreen: Screen("charging_history")
    object VehicleDetailsScreen : Screen("vehicle_details")
    object NONE : Screen("") // a sort of null value

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
