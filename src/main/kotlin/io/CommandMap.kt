package io

import kotlinx.serialization.Serializable

@Serializable
class CommandMap(val commands: List<String>, val subs: List<String>) {
    fun getSubFromCommand(cmd: String): String {
        val index = commands.indexOf(cmd)
        if (index == -1) {
            throw UnknownCommand(cmd)
        }
        return subs[index]
    }
}