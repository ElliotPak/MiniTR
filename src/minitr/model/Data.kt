/**
 * Name: Data.kt
 * Contains various data classes.
 * @author Elliot Pak
 */

package minitr.model

/**
 * Contains the settings of a minitr project.
 */
data class Settings(var name: String, var root: String,
                    var tmuxCommand: String, var tmuxFlags: String,
                    var startWindow: String)

/**
 * Contains the commands executed at various points of a minitr project.
 */
data class Commands(var start: Array<String>, var attach: Array<String>,
                    var detatch: Array<String>, var stop: Array<String>,
                    var preWindow: Array<String>)

/**
 * Contains information about a tmux window
 */
data class Window(var name: String, var layout: String,
                  var panes: Array<String>)

/**
 * Contains all information about a minitr project
 */
data class Project(val settings: Settings, val commands: Commands,
                   val windows: List<Window>)
