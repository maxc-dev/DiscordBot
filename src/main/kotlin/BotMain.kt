import dev.kord.core.Kord

class BotActivity(private val token: String) {
    private lateinit var kord: Kord

    suspend fun start() {
        kord = Kord(token)
    }

    suspend fun stop() {
        kord.shutdown()
    }

}