import activities.BotActivity
import dev.kord.core.Kord
import dev.kord.rest.service.RestClient
import input.executables.ExecutableInput

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

    suspend fun executeInput(args: String, input: ExecutableInput): Status {
        return input.execute(args, this)
    }

    suspend fun executeActivity(instruction: BotActivity, args: String): Status {
        return instruction.execute(args)
    }
}