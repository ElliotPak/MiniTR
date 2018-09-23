package net.elliotpak.minitr

/**
 * Contains all commands that are to be executed
 */
class CommandManager() {
    val commandList: MutableList<List<String>> = mutableListOf()

    fun addCommand(command: List<String>) {
        commandList.add(command)
    }

    fun commandToString(command: List<String>): String {
        var toAdd = StringBuilder()
        for (cc in command) {
            toAdd.append("$cc")
            if (cc != command.last()) toAdd.append(" ")
        }
        return toAdd.toString()
    }

    fun getCommands(): List<List<String>> {
        val toReturn: List<List<String>> = commandList
        return toReturn
    }

    fun getCommandsAsStrings(): List<String> {
        val list: MutableList<String> = mutableListOf()
        for (command in commandList) {
            list.add(commandToString(command))
        }
        return list
    }
}
