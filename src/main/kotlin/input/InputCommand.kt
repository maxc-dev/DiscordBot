package input

import activities.BroadcastMessageActivity
import input.executables.ExecutableInput
import input.executables.ExecutableInput.SimpleExecutableCommand
import input.executables.ExitCommand

enum class InputCommand(val command: String, val executable: ExecutableInput, val description: String = "") {
    EXIT(InputDefinition.EXIT, ExitCommand, InputDefinition.EXIT_DESCRIPTION),
    BROADCAST_MESSAGE(
        InputDefinition.BROADCAST_MESSAGE,
        SimpleExecutableCommand(::BroadcastMessageActivity),
        InputDefinition.BROADCAST_MESSAGE_DESCRIPTION
    ),
}

/**
 * Wrap enum in map to make input lookups constant
 */
object InputCommandManager {
    val inputMapper =
        InputCommand.values().associate { it.command to it.executable }.toMap()
}