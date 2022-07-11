package activities

import BotMain
import Status

abstract class BotActivity(bot: BotMain) {
    val kord = bot.kord
    abstract suspend fun execute(args: String): Status
}