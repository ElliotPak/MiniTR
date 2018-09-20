package net.elliotpak.minitr

import java.io.File

fun main(args: Array<String>) {
    if (args.size == 1) {
        executeProjectAction(".minitr", args[0])
    }
    else if (args.size == 2) {
        executeProjectAction("~/.config/minitr/${args[1]}.minitr", args[0])
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
    }
    else {
        val optionsStart: MutableMap<String, (Project) -> Unit> = HashMap()
        optionsStart.put("start", ::projectStart)
        optionsStart.put("start-bg", ::projectCreate)
        optionsStart.put("attach", ::projectAttach)
        optionsStart.put("debug", ::projectDebug)
        val cont = getFileContents(projectStr)
        val project = parseProjectFile(cont)
        val actionFunc = optionsStart.get(action)
        if (actionFunc != null) {
            actionFunc(project)
        }
    }
}

fun projectCreate(project: Project): Unit {
    val cm = CommandManager()
    setupMinitrProject(project, cm)
    val commands = cm.getCommands()
    for (c in commands) {
        c.runCommand()
    }
}

fun projectStart(project: Project): Unit {
    projectCreate(project)
    projectAttach(project)
}

fun projectAttach(project: Project): Unit  {
    val command = buildAttachCommand(project.settings)
    command.runCommandInteractive()
}

fun projectDebug(project: Project): Unit {
    val cm = CommandManager()
    setupMinitrProject(project, cm)
    val commands = cm.getCommandsAsStrings()
    for (c in commands) {
        println(c)
    }
}

fun setupMinitrProject(project: Project, commands: CommandManager): Unit {
    val settings = project.settings
    commands.addCommand(buildStartCommand(settings))
    for (window in project.windows) {
        if (window == project.windows.first()) {
            commands.addCommand(buildWindowRenameCommand(settings, window))
        }
        else {
            commands.addCommand(buildNewWindowCommand(settings, window))
        }
        for (pane in window.panes) {
            commands.addCommand(buildExecuteCommand(settings, pane))
            if (pane != window.panes.last()) {
                commands.addCommand(buildSplitCommand(settings))
            }
        }
        if (window.layout != "") {
            commands.addCommand(buildLayoutCommand(settings, window.layout))
        }
    }
}

fun projectNew(projectName: String): Unit {
    saveFile("~/.config/minitr/$projectName.minitr",
        createProjectSkeleton(projectName, "~/.config/minitr"))
}

fun projectNewLocal(projectName: String): Unit {
    val userDir = System.getProperty("user.dir")
    saveFile("$userDir/.minitr", createProjectSkeleton(projectName, userDir))
}
