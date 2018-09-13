package minitr

import minitr.external.*
import minitr.model.Project
import minitr.model.CommandInfo

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
    val commands = CommandInfo(true)
    val optionsNew: MutableMap<String, (String, CommandInfo) -> Unit> = HashMap()
    optionsNew.put("new", ::projectNew)
    optionsNew.put("new-local", ::projectNewLocal)
    val actionNewFunc = optionsNew.get(action)

    if (actionNewFunc != null) {
        val results = actionNewFunc(projectStr, commands)
        println(results)
    }
    else {
        val optionsStart: MutableMap<String, (Project, CommandInfo) -> Unit> = HashMap()
        optionsStart.put("start", ::projectStart)
        optionsStart.put("start-bg", ::projectCreate)
        optionsStart.put("attach", ::projectAttach)
        optionsStart.put("debug", ::projectDebug)
        val cont = getFileContents(projectStr)
        val project = parseProjectFile(cont)
        val actionFunc = optionsStart.get(action)
        if (actionFunc != null) {
            actionFunc(project, commands)
        }
    }
}

fun projectCreate(project: Project, commands: CommandInfo): Unit {
    setupMinitrProject(project, commands)
}

fun projectStart(project: Project, commands: CommandInfo): Unit {
    projectCreate(project, commands)
    projectAttach(project, commands)
}

fun projectAttach(project: Project, commands: CommandInfo): Unit  {
    val command = buildAttachCommand(project.settings)
    commands.addCommand(command)
    command.runCommandInteractive()
}

fun projectDebug(project: Project, commands: CommandInfo): Unit {
    commands.shouldExecute = false
    setupMinitrProject(project, commands)
    for (command in commands.getCommands()) {
        println(command)
    }
}

fun setupMinitrProject(project: Project, commands: CommandInfo): Unit {
    val settings = project.settings
    executeSetupCommand(buildStartCommand(settings), commands)
    for (window in project.windows) {
        if (window == project.windows.first()) {
            executeSetupCommand(buildWindowRenameCommand(settings, window), commands)
        }
        else {
            executeSetupCommand(buildNewWindowCommand(settings, window), commands)
        }
        for (pane in window.panes) {
            executeSetupCommand(buildExecuteCommand(settings, pane), commands)
            if (pane != window.panes.last()) {
                executeSetupCommand(buildSplitCommand(settings), commands)
            }
        }
        if (window.layout != "") {
            executeSetupCommand(buildLayoutCommand(settings, window.layout), commands)
        }
    }
}

fun projectNew(projectName: String, commands: CommandInfo): Unit {
    saveFile("~/.config/minitr/$projectName.minitr", createProjectSkeleton(projectName, "~/.config/minitr"))
}

fun projectNewLocal(projectName: String, commands: CommandInfo): Unit {
    println("whoo")
    val userDir = System.getProperty("user.dir")
    saveFile("$userDir/.minitr", createProjectSkeleton(projectName, userDir))
}

fun executeSetupCommand(command: List<String>, commands: CommandInfo): Unit {
    commands.addCommand(command)
    if (commands.shouldExecute) {
        command.runCommand()
    }
}
