package org.liamjd.amber.screens.state

import org.liamjd.amber.screens.Screen

/**
 * Represents the different states the UI can be in, such as Loading or Navigating (to another screen)
 */
sealed class UIState {
    /**
     * Active state is the default, where a screen is just doing its thing
     */
    object Active: UIState()

    /**
     * Use the Loading state when fetching data and not ready to display the content
     */
    object Loading: UIState()

    /**
     * Set to Navigating state when preparing to navigate to a different screen
     * @param nextScreen the screen you are navigating to
     */
    class Navigating(val nextScreen: Screen, val backScreen: Screen? = Screen.StartScreen) : UIState()
}
