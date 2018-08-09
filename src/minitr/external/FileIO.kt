/**
 * Name: ExternalInteract.kt
 * Contains functions for interacting with things external to the program,
 * such as files and commands.
 * @author Elliot Pak
 */

package minitr.external

import minitr.model.*
import java.io.*

/**
 * Gets the contents of a file.
 * @param filename the name of the file
 * @return the contents of the file
 */
fun getFileContents(filename: String): String {
    val bufferedReader: BufferedReader = File(filename).bufferedReader()
    val inputString = bufferedReader.use {it.readText()}
    return inputString
}

/**
 * Parses project file contents and returns a Project object.
 * @return A Project representing the current minitr project
 */

fun parseProjectFile(contents: String): Project {
    var settings = Settings("", ".", "tmux", "", "", arrayOf())
    var commands = Commands(arrayOf(), arrayOf(), arrayOf(), arrayOf(), arrayOf())
    val windows: MutableList<Window> = mutableListOf()
    val windowsRead: List<Window> = windows
    val regWindow = Regex("^window\\/(.*)]\n")

    val lines = contents.split("[")
    for (l in lines) {
        if (l.startsWith("settings]")) {
            settings = parseSettings(l)
        }
        if (l.startsWith("commands]")) {
            commands = parseCommands(l)
        }
        else {
            val match = regWindow.find(l)
            if (match != null &&  match.groups[1]?.value != null) {
                windows.add(parseWindow(match.groups[1]?.value ?: "", l))
            }
        }
    }
    
    return Project(settings, commands, windowsRead)
}

fun parseSettings(input: String): Settings {
    var settings = Settings("", ".", "tmux", "", "", arrayOf())

    for (l in input.split("\n")) {
        val (key, value) = parseConfigLine(l)
        if (key == "name") settings.name = value
        if (key == "root") settings.root = value
        if (key == "tmux-command") settings.tmuxCommand = value
        if (key == "tmux-flags") settings.tmuxFlags = value
        if (key == "start-window") settings.startWindow = value
        if (key == "windows") settings.windows = parseQuoteList(value)
    }

    return settings
}

fun parseCommands(input: String): Commands {
    var commands = Commands(arrayOf(), arrayOf(), arrayOf(), arrayOf(), arrayOf())

    for (l in input.split("\n")) {
        val (key, value) = parseConfigLine(l)
        if (key == "start") commands.start = parseQuoteList(value)
        if (key == "attach") commands.attach = parseQuoteList(value)
        if (key == "detatch") commands.detatch = parseQuoteList(value)
        if (key == "stop") commands.stop = parseQuoteList(value)
        if (key == "pre-window") commands.preWindow = parseQuoteList(value)
    }

    return commands
}

fun parseWindow(name: String, input: String): Window {
    var windows = Window(name, "", arrayOf())

    for (l in input.split("\n")) {
        val (key, value) = parseConfigLine(l)
        if (key == "layout") windows.layout = value
        if (key == "panes") windows.panes = parseQuoteList(value)
    }

    return windows
}

fun parseConfigLine(line: String): Pair<String, String> {
    var key = ""
    var value = ""
    val regSetting = Regex("^([A-z\\-]*)\\s*=\\s*(.*)")
    val match = regSetting.find(line)
    if (match != null) {
        key = match.groups[1]?.value ?: ""
        value = match.groups[2]?.value ?: ""
    }
    return Pair(key, value)
}

fun parseQuoteList(value: String): Array<String> {
    return value.split("\\s*\"\\s*").toTypedArray()
}