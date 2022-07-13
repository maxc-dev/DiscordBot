import input.InputScanner

suspend fun main() {
    Main().start()
}

class Main {
    private val log = logger(this.javaClass)

    suspend fun start() {
        log.info("Process started")
        val token = TokenRetriever().retrieveToken()
        val botMain = BotMain(token)

        botMain.start()
        val inputScanner = InputScanner(botMain)
        inputScanner.listen()

        // application closes when inputScanner.listen() returns
        botMain.stop()
    }
}
