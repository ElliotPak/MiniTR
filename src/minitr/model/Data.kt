/**
 * Name: Data.kt
 * Contains various data classes.
 */

package minitr.model

/**
 * Contains the settings of a minitr project.
 */
data class Settings(val name: String, val root: String,
                    val tmuxCommand: String, val tmuxFlags: String,
                    val startWindow: String)

/**
 * Contains the commands executed at various points of a minitr project.
 */
data class Commands(val start: Array<String>, val attach: Array<String>,
                    val detatch: Array<String>, val stop: Array<String>,
                    val preWindow: Array<String>)

/**
 * Contains information about a tmux window
 */
data class Window(val name: String, val layout: String = "",
                  val panes: Array<String>)

/**
 * Contains all information about a minitr project
 */
data class Project(val settings: Settings, val commands: Commands,
                   val windows: Array<Window>)
