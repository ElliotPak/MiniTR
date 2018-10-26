/**
 * Name: FileIO.kt
 * Contains functions related to reading and writing files.
 * @author Elliot Pak
 */

package net.elliotpak.minitr

import com.beust.klaxon.Klaxon
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
 * Returns true if the specified file exists.
 */
fun doesFileExist(filename: String): Boolean {
    val file: File = File(filename)
    return file.isFile()
}

/**
 * Creates a string for a basic project file.
 */
fun createProjectSkeleton(projectName: String, projectPath: String): String {
    return """[settings]
name = $projectName
root = $projectPath
tmux-command = tmux
tmux-flags = 
start-window = default

[window/default]
panes = ""
"""
}

/**
 * Saves file contents to specified file.
 */
fun saveFile(filename: String, contents: String) {
    File(filename).printWriter().use { out ->
        out.write(contents)
    }
}

/**
 * Parses project file contents and returns a Project object.
 * @return A Project representing the current minitr project
 */

fun parseProjectFile(contents: String): Project? {
    return Klaxon().parse<Project>(contents)
}
