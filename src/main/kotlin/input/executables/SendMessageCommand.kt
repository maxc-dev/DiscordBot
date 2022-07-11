package input.executables

import BotMain
import Status

object SendMessage : ExecutableInput() {
    /**
     * Shuts down discord bot and closes application
     */
    override suspend fun execute(command: String, bot: BotMain): Status {
        bot.executeActivity(SendMessage::execute)
        return Status.SUCCESS
    }
}