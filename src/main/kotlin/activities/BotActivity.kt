package activities

import BotMain
import Status

abstract class BotActivity(bot: BotMain) {
    val kord = bot.kord
    val rest = bot.rest
    abstract suspend fun execute(args: String): Status
}