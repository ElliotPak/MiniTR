package minitr

import minitr.external.*
import minitr.model.Project

fun main(args: Array<String>) {
    if (args.size == 1) {
        val cont = getFileContents(".minitr")
        val project = parseProjectFile(cont)
        executeProjectAction(project, args[0]);
    }
    else if (args.size == 2) {
        val cont = getFileContents("~/.config/minitr/${args[0]}.minitr")
        val project = parseProjectFile(cont)
        executeProjectAction(project, args[1]);
    }
    else {
        println("Please enter two arguments")
    }
}

fun executeProjectAction(project: Project, action: String) {
    val options: MutableMap<String, (Project) -> String?> = HashMap()
    options.put("start", ::projectStart)
    options.put("start-bg", ::projectCreate)
    options.put("attach", ::projectAttach)
    options.put("debug", ::projectDebug)
    val actionFunc: ((Project) -> String?)? = options.get(action)
    if (actionFunc != null) {
        val results = actionFunc(project)
        println(results)
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
        results += executeSetupCommand(buildNewWindowCommand(settings, window), debug)
    }
    return results
}

fun executeSetupCommand(command: String, debug: Boolean = false): String? {
    if (!debug) {
        command.runCommand()
        return null
    }
    return "$command\n"
}
