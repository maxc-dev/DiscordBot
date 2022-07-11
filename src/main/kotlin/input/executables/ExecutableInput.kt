package input.executables

import BotMain
import Status

sealed class ExecutableInput {
    abstract suspend fun execute(args: String, bot: BotMain): Status
}