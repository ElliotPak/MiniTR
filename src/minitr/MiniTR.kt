package minitr

import minitr.external.*

fun main(args: Array<String>) {
    val cont = getFileContents(".minitr")
    val command = buildExecuteCommand(parseProjectFile(cont).settings, "some kinda test")
    command.runCommand()
}
