@file:OptIn(DelicateCoroutinesApi::class)

import input.InputScanner
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main() {
    val token = TokenRetriever().retrieveToken()
    val botMain = BotMain(token)

    GlobalScope.launch {
        botMain.start()
    }

    val inputScanner = InputScanner(botMain)
    inputScanner.listen()
    // application closing...
    GlobalScope.launch {
        botMain.stop()
    }

}