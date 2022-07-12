@file:OptIn(DelicateCoroutinesApi::class)

import input.InputScanner
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

suspend fun main() {
    val token = TokenRetriever().retrieveToken()
    val botMain = BotMain(token)

    GlobalScope.launch {
        botMain.start()
    }

    val inputScanner = InputScanner(botMain)
    inputScanner.listen()

    // application closes when inputScanner.listen() returns
    botMain.stop()
}