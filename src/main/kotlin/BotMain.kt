import dev.kord.core.Kord
import dev.kord.rest.service.RestClient

class BotMain(private val token: String) {
    private val log = logger(this.javaClass)

    lateinit var kord: Kord
        private set
    lateinit var rest: RestClient
        private set

    suspend fun start() {
        log.info("Starting bot...")
        kord = Kord(token)
        rest = RestClient(token)
    }

    suspend fun stop() {
        log.info("Stopping bot...")
        kord.shutdown()
    }
}