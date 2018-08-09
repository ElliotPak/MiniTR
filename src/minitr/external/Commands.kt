package minitr.external

import minitr.model.*
import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit
import java.io.File

/**
 * Executes an interactive command in the specified working directory,
 * @param workingDir The working directory to execute the command in
 */
fun String.runCommandInteractive(workingDir: File? = null) {
    val process = ProcessBuilder(*split(" ").toTypedArray())
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
    val process = ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start()
     process.waitFor(60, TimeUnit.MINUTES)
     val processOut = process.inputStream.bufferedReader().readText()
     val processErr = process.errorStream.bufferedReader().readText()
     return Pair(processOut, processErr)
}