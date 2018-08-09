package minitr

import minitr.external.*

fun main(args: Array<String>) {
    val cont = getFileContents(".minitr")
    println(parseProjectFile(cont))
}
