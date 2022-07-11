package input.executables

import BotMain
import Status

object ExitCommand : ExecutableInput() {
    /**
     * Shuts down discord bot and closes application
     */
    override suspend fun execute(args: String, bot: BotMain): Status {
        // shutting down of the bot is handled by the botMain instance in Main.kt
        return Status.CLOSE
    }
}