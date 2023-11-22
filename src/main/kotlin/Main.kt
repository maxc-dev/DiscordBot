import io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File

/**
 * @param args Argument pattern:
 *  - Token file
 *  - Settings file
 *  - Command mappings file
 */
fun main(args: Array<String>) {
    if (args.size < 3) {
        throw IllegalArgumentException("Required arguments for token, settings and command map file. Only found ${args.size}")
    }
    Launcher().start(args)
}

class Launcher {
    private val log = logger(this.javaClass)

    fun start(args: Array<String>) = runBlocking {
        log.info("Process started")
        val token: String = TokenRetriever().retrieveToken(File(args[0]))
        val credentials = Json.decodeFromString<Credentials>(File(args[1]).readText())
        val commandMap = Json.decodeFromString<CommandMap>(File(args[2]).readText())
        val commandManager = CommandManager(commandMap, credentials)

        val botMain = BotMain(token, commandManager)
        botMain.start() // start the bot

/*        val inputScanner = InputScanner(botMain)
        inputScanner.listen()

        // application closes when inputScanner.listen() returns
        botMain.stop()
        log.info("Process stopped")*/
    }
}
