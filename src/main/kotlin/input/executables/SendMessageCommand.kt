package input.executables

import BotMain
import Status
import activities.SendMessageActivity

object SendMessageCommand : ExecutableInput() {
    /**
     * Shuts down discord bot and closes application
     */
    override suspend fun execute(args: String, bot: BotMain): Status {
        return bot.executeActivity(SendMessageActivity(bot), args)
    }
}