package input

import input.executables.ExecutableInput
import input.executables.ExitCommand
import input.executables.SendMessageCommand

enum class InputCommand(val command: String, val executable: ExecutableInput, val description: String = "") {
    EXIT(InputDefinition.EXIT, ExitCommand, InputDefinition.EXIT_DESCRIPTION),
    SEND_MESSAGE(InputDefinition.SEND_MESSAGE, SendMessageCommand, InputDefinition.SEND_MESSAGE_DESCRIPTION)
}

/**
 * Wrap enum in map to make input lookups constant
 */
object InputCommandManager {
    val inputMapper =
        InputCommand.values().associate { it.command to it.executable }.toMap()
}