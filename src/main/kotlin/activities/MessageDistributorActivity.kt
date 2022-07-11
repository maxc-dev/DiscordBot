package activities

import BotMain
import Status

open class MessageDistributorActivity(bot: BotMain) : BotActivity(bot) {
    fun getId(args: String): String {
        return args.substringBefore(" ")
    }

    override suspend fun execute(args: String): Status {
        return Status.FAILURE
    }
}