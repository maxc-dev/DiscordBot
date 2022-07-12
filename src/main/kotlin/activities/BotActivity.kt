package activities

import BotMain
import Status

abstract class BotActivity {
    abstract suspend fun execute(args: String, bot: BotMain): Status
}