/**
 * Name: ExternalInteract.kt
 * Contains functions for interacting with things external to the program,
 * such as files and commands.
 * @author Elliot Pak
 */

package minitr.external

import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit

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
fun String.runCommand(workingDir: File? = null): Pairr<String, String> {
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
