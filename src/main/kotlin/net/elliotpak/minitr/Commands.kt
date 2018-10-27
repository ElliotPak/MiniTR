package net.elliotpak.minitr

import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit
import java.io.File

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

/**
 * Replaces the tilde in front of a string with the user's home, among other
 * path fixes (like using .).
 *
 * Fun fact: this took me like 2 months to solve. At the time of writing this,
 * I just solved it, and I'm so gosh darn mad it took me this long you have no
 * idea.
 *
 * Note to future Elliot: Never use a tilde anywhere outside of bash, and even
 * then, don't do it. I'll be VERY sad.
 */
fun sanitisePath(tilded: String): String {
    val stage1 = tilded.replace(Regex("^~"), System.getProperty("user.home"))
    val stage2 = stage1.replace(Regex("^\\."), System.getProperty("user.dir"))
    return stage2
}

fun buildStartCommand(settings: Settings): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("new-session")
    command.add("-d")
    command.add("-c")
    command.add("${sanitisePath(settings.root)}")
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

fun buildSplitCommand(settings: Settings, pane: Pane): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("split-window")
    command.add("-c")
    command.add(sanitisePath(pane.dir))
    return command
}

fun buildKillInitialPaneCommand(settings: Settings): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("kill-pane")
    command.add("-t")
    command.add("0")
    return command
}

fun buildKillOtherWindowsCommand(settings: Settings, windowId: String): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("kill-window")
    command.add("-a")
    command.add("-t")
    command.add(windowId)
    return command
}

fun buildReorderWindowsCommand(settings: Settings): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("move-window")
    command.add("-r")
    return command
}

fun buildLayoutCommand(settings: Settings, layout: String): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("select-layout")
    command.add("$layout")
    return command
}

fun buildExecuteCommand(settings: Settings, toExecute: Array<String>): List<String> {
    val command: MutableList<String> = mutableListOf()
    command.add("${settings.tmuxCommand}")
    command.add("send-keys")
    var commandString = ""
    for (line in toExecute) {
        if (line == toExecute.last()) {
            commandString += "$line\n"
        }
        else {
            commandString += "$line;"
        }
    }
    command.add(commandString)
    return command
}
