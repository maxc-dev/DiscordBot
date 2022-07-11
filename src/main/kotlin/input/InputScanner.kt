package input

import BotMain
import Status
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class InputScanner(private val bot: BotMain) {
    fun listen() {
        val scanner = Scanner(System.`in`)
        while (scanner.hasNext()) {
            val input = scanner.next()
            val status = enact(input)

            // if the app is to close, return to main
            if (status.showHelp) help()
            else if (status == Status.CLOSE) return
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun enact(input: String): Status {
        if (input.isBlank()) return Status.UNKNOWN

        val firstArg = input.plus(" ").substringBefore(" ").lowercase()
        // parse input string to command
        val command: InputCommand = InputCommand.values().firstOrNull { it.command == firstArg } ?: return Status.UNKNOWN

        //convert input to args
        val args = input.substringAfter(firstArg).trim()

        // create new coroutine for executing command and return status
        var status = Status.FAILURE
        GlobalScope.launch {
            status = bot.executeInput(args, command.executable)
        }
        println(status.statement())
        return status
    }

    private fun help() {
        println("Available commands:")
        InputCommand.values().forEach { println(" > ${it.command}: ${it.description}") }
    }
}