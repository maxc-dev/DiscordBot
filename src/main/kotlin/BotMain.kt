import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.rest.service.RestClient
import io.CommandManager
import io.UnknownCommand

class BotMain(private val token: String, private val commandManager: CommandManager) {
    private val log = logger(this.javaClass)

    private lateinit var kord: Kord
        private set
    lateinit var rest: RestClient
        private set

    suspend fun start() {
        log.info("Starting bot...")
        kord = Kord(token)
        rest = RestClient(token)

        kord.on<MessageCreateEvent> {
            if (message.content.startsWith("!")) {
                try {
                    message.channel.createMessage(commandManager.retrieve(message.content).getUrl())
                } catch (e: UnknownCommand) {
                    log.warning(e.localizedMessage)
                }
            }
        }
    }

    suspend fun stop() {
        log.info("Stopping bot...")
        kord.shutdown()
    }
}