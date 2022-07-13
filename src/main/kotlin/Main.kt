import input.InputScanner
import kotlinx.coroutines.runBlocking

fun main() {
    Launcher().start()
}

class Launcher {
    private val log = logger(this.javaClass)

    fun start() = runBlocking {
        log.info("Process started")
        val token = TokenRetriever().retrieveToken()

        val botMain = BotMain(token)
        botMain.start() // start the bot

        val inputScanner = InputScanner(botMain)
        inputScanner.listen()

        // application closes when inputScanner.listen() returns
        botMain.stop()
        log.info("Process stopped")
    }
}
