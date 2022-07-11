package input.executables

import BotMain
import Status

object Exit : ExecutableInput() {
    /**
     * Shuts down discord bot and closes application
     */
    override suspend fun execute(command: String, bot: BotMain): Status {
        // shutting down of the bot is handled by the botMain instance in Main.kt
        return Status.CLOSE
    }
}