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
    return """ {
    "settings": {
        "name": "minitr",
        "root": "~/projects/minitr",
        "tmuxCommand": "tmux",
        "tmuxFlags": "",
        "startWindow": "window"
    },
    "commands": {
    },
    "windows": [
        {
            "name": "window",
            "layout": "main-vertical",
            "panes": [ ]
        }
    ]
}
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
    val proj = Klaxon().parse<Project>(contents)
    if (proj != null) {
        proj.settings.root = sanitisePath(proj.settings.root)
        for (window in proj.windows) {
            for (pane in window.panes) {
                pane.dir = "${proj.settings.root}/${sanitisePath(pane.dir)}"
            }
        }
    }
    return proj
}
