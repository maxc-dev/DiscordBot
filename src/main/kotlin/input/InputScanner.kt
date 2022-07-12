package input

import BotMain
import Status
import input.executables.ExecutableInput
import logger
import java.util.*

class InputScanner(private val bot: BotMain) {
    private val log = logger(this.javaClass)

    suspend fun listen() {
        val scanner = Scanner(System.`in`)
        log.info("Enter command: ")
        while (scanner.hasNext()) {
            val input = scanner.nextLine().trim()
            val status = enact(input)

            // if the app is to close, return to main
            if (status.showHelp) help()
            else if (status == Status.CLOSE) return
        }
    }

    private suspend fun enact(input: String): Status {
        if (input.isBlank()) return Status.UNKNOWN_COMMAND

        val firstArg = input.plus(" ").substringBefore(" ").lowercase()
        // parse input string to command
        if (!InputCommandManager.inputMapper.containsKey(firstArg)) {
            return Status.UNKNOWN_COMMAND
        }
        val executableCommand: ExecutableInput =
            InputCommandManager.inputMapper[firstArg] ?: return Status.UNKNOWN_COMMAND

        //convert input to args
        val args = input.substringAfter(firstArg).trim()
        val status = executableCommand.execute(args, bot)
        log.info(status.statement())
        return status
    }

    private fun help() {
        log.info(
            "Available commands:" + InputCommand.values()
                .joinToString(separator = "\n", prefix = "\n") { " > ${it.command}: ${it.description}" })
    }
}