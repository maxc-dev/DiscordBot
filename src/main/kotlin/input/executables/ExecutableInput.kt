package input.executables

import BotMain
import Status
import activities.BotActivity

sealed class ExecutableInput {
    abstract suspend fun execute(args: String, bot: BotMain): Status

    class SimpleExecutableCommand<T : BotActivity>(val factory: () -> T) : ExecutableInput() {
        override suspend fun execute(args: String, bot: BotMain): Status {
            return factory().execute(args, bot)
        }
    }
}