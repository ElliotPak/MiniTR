package minitr.external

import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit
import java.io.File
import minitr.model.*

/**
 * Executes an interactive command in the specified working directory.
 * @param workingDir The working directory to execute the command in
 */
fun String.runCommandInteractive(workingDir: File? = null) {
    this.getCommandArray().runCommandInteractive(workingDir)
}

/**
 * Executes an interactive command in the specified working directory.
 * @param workingDir The working directory to execute the command in
 */
fun List<String>.runCommandInteractive(workingDir: File? = null) {
    val process = ProcessBuilder(this)
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
    return this.getCommandArray().runCommand(workingDir)
}

/**
 * Executes a non-interactive command in the specified working directory,
 * keeping track of stdout and stderr.
 * @param workingDir The working directory to execute the command in
 * @return A Pair containing the contents of stdout and stderr
 */
fun List<String>.runCommand(workingDir: File? = null): Pair<String, String> {
    val process = ProcessBuilder(this)
            .directory(workingDir)
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start()
     process.waitFor(60, TimeUnit.MINUTES)
     val processOut = process.inputStream.bufferedReader().readText()
     val processErr = process.errorStream.bufferedReader().readText()
     return Pair(processOut, processErr)
}

/**
 * Converts a string into a list suitable for ProcessBuilder
 */
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

fun buildStartCommand(settings: Settings): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("new-session")
    command.add("-d")
    command.add("-c")
    command.add("${settings.root}")
    command.add("-s")
    command.add("${settings.name}")
    return command
}

fun buildAttachCommand(settings: Settings): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("attach")
    command.add("-t")
    command.add("${settings.name}")
    return command
}

fun buildNewWindowCommand(settings: Settings, window: Window): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("new-window")
    command.add("-n")
    command.add("${window.name}")
    return command
}

fun buildWindowRenameCommand(settings: Settings, window: Window): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("rename-window")
    command.add("${window.name}")
    return command
}

fun buildSplitCommand(settings: Settings): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("split-window")
    return command
}

fun buildLayoutCommand(settings: Settings, layout: String): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("select-layout")
    command.add("$layout")
    return command
}

fun buildExecuteCommand(settings: Settings, toExecute: String): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("send-keys")
    command.add("$toExecute\n")
    return command
}
