import activities.BotActivity
import dev.kord.core.Kord
import input.executables.ExecutableInput

class BotMain(private val token: String) {
    lateinit var kord: Kord
        private set

    suspend fun start() {
        kord = Kord(token)
    }

    suspend fun stop() {
        kord.shutdown()
    }

    suspend fun executeInput(args: String, input: ExecutableInput): Status {
        return input.execute(args, this)
    }

    suspend fun executeActivity(instruction: BotActivity, args: String): Status {
        return instruction.execute(args)
    }
}