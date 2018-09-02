package minitr.external

import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit
import java.io.File
import minitr.model.*

/**
 * Executes an interactive command in the specified working directory,
 * @param workingDir The working directory to execute the command in
 */
fun String.runCommandInteractive(workingDir: File? = null) {
    val process = ProcessBuilder(this.getCommandArray())
            .directory(workingDir)
            .redirectInput(Redirect.INHERIT)
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start()
     process.waitFor(60, TimeUnit.MINUTES)
}

/**
 * Executes a non-interactive command in the specified working directory,
 * keeping track of stdout and stderr.
 * @param workingDir The working directory to execute the command in
 * @return A Pair containing the contents of stdout and stderr
 */
fun String.runCommand(workingDir: File? = null): Pair<String, String> {
    val process = ProcessBuilder(this.getCommandArray())
            .directory(workingDir)
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start()
     process.waitFor(60, TimeUnit.MINUTES)
     val processOut = process.inputStream.bufferedReader().readText()
     val processErr = process.errorStream.bufferedReader().readText()
     return Pair(processOut, processErr)
}

fun String.getCommandArray(): List<String> {
    val commandsOld = this.split(Regex("(?<!\\\\) "))

    // now we need to get rid of backslashes properly
    val commandsMut : MutableList<String> = mutableListOf()
    val commands : List<String> = commandsMut
    for (ii in 0..(commandsOld.size - 1)) {
        if (commandsOld[ii] == "\\ ") {
            // fix escaped spaces
            commandsMut.add(" ")
        }
        else {
            commandsMut.add(commandsOld[ii])
        }
    }
    return commands
}

fun buildStartCommand(settings: Settings): String {
    var command = "${settings.tmuxCommand} new-session -d"
    command += " -c ${settings.root}"
    command += " -s ${settings.name}"
    return command
}

fun buildAttachCommand(settings: Settings): String {
    var command = "${settings.tmuxCommand} attach"
    command += " -t ${settings.name}"
    return command
}

fun buildNewWindowCommand(settings: Settings, window: Window): String {
    var command = "${settings.tmuxCommand} new-window"
    if (settings.startWindow != window.name) {
        command += " -d"
    }
    command += " -n ${window.name}"
    return command
}

fun buildExecuteCommand(settings: Settings, toExecute: String): String {
    var command = "${settings.tmuxCommand} send-keys -l"
    for (char in toExecute) {
        if (char == ' ') {
            command += """ \ """
        }
        else {
            command += " $char"
        }
    }
    command += "\n"
    return command
}
