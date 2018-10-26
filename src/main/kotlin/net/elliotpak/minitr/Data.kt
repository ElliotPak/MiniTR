/**
 * Name: Data.kt
 * Contains various data classes.
 * @author Elliot Pak
 */

package net.elliotpak.minitr

/**
 * Contains the settings of a minitr project.
 */
data class Settings(var name: String, var root: String,
                    var tmuxCommand: String, var tmuxFlags: String,
                    var startWindow: String)

/**
 * Contains the commands executed at various points of a minitr project.
 */
data class Commands(var start: Array<String> = arrayOf(),
                    var attach: Array<String> = arrayOf(),
                    var detatch: Array<String> = arrayOf(),
                    var stop: Array<String> = arrayOf(),
                    var preWindow: Array<String> = arrayOf())

/**
 * Contains information about a tmux window
 */
data class Window(var name: String, var layout: String = "",
                  var panes: Array<Pane>)

/**
 * Contains information about a tmux pane
 */

data class Pane(var dir: String = "", var commands: Array<String>)

/**
 * Contains all information about a minitr project
 */
data class Project(val settings: Settings, val commands: Commands,
                   val windows: List<Window>)

