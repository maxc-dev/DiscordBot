package input

import dev.kord.core.Kord

sealed class ExecutableInput(internal val command: String) {
    abstract suspend fun execute(kord: Kord): Status
}