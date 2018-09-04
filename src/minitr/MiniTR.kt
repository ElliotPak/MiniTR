package minitr

import minitr.external.*
import minitr.model.Project

fun main(args: Array<String>) {
    if (args.size == 1) {
        executeProjectAction(".minitr", args[0]);
    }
    else if (args.size == 2) {
        executeProjectAction("~/.config/minitr/${args[1]}.minitr", args[0]);
    }
    else {
        println("Please enter two arguments")
    }
}

fun executeProjectAction(projectStr: String, action: String) {
    val optionsNew: MutableMap<String, (String) -> Unit> = HashMap()
    optionsNew.put("new", ::projectNew)
    optionsNew.put("new-local", ::projectNewLocal)
    val actionNewFunc = optionsNew.get(action)
    if (actionNewFunc != null) {
        val results = actionNewFunc(projectStr)
        println(results)
    }
    else {
        val optionsStart: MutableMap<String, (Project) -> String?> = HashMap()
        optionsStart.put("start", ::projectStart)
        optionsStart.put("start-bg", ::projectCreate)
        optionsStart.put("attach", ::projectAttach)
        optionsStart.put("debug", ::projectDebug)
        val cont = getFileContents(projectStr)
        val project = parseProjectFile(cont)
        val actionFunc = optionsStart.get(action)
        if (actionFunc != null) {
            val results = actionFunc(project)
            println(results)
        }
    }
}

fun projectCreate(project: Project): String? {
    setupMinitrProject(project)
    return null
}

fun projectStart(project: Project): String? {
    projectCreate(project)
    projectAttach(project)
    return null
}

fun projectAttach(project: Project): String?  {
    buildAttachCommand(project.settings).runCommandInteractive()
    return null
}

fun projectDebug(project: Project): String? {
    return setupMinitrProject(project, debug=true)
}

fun setupMinitrProject(project: Project, debug: Boolean = false): String? {
    var results = ""
    val settings = project.settings
    results += executeSetupCommand(buildStartCommand(settings), debug)
    for (window in project.windows) {
        if (window == project.windows.first()) {
            results += executeSetupCommand(buildWindowRenameCommand(settings, window), debug)
        }
        else {
            results += executeSetupCommand(buildNewWindowCommand(settings, window), debug)
        }
        for (pane in window.panes) {
            results += executeSetupCommand(buildExecuteCommand(settings, pane), debug)
            if (pane != window.panes.last()) {
                results += executeSetupCommand(buildSplitCommand(settings), debug)
            }
        }
        if (window.layout != "") {
            results += executeSetupCommand(buildLayoutCommand(settings, window.layout), debug)
        }
    }
    return results
}

fun projectNew(projectName: String): Unit {
    saveFile("~/.config/minitr/$projectName.minitr", createProjectSkeleton(projectName))
}

fun projectNewLocal(projectName: String): Unit {
    saveFile(".minitr", createProjectSkeleton(projectName))
}

fun executeSetupCommand(command: String, debug: Boolean = false): String? {
    if (!debug) {
        command.runCommand()
        return null
    }
    return "$command\n"
}
