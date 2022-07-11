package activities

import BotMain
import Status

abstract class BotCommandListener(private val bot: BotMain) {
    abstract suspend fun execute(args: String): Status
}