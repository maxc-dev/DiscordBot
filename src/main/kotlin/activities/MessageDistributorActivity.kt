package activities

import BotMain
import Status

open class MessageDistributorActivity : BotActivity() {
    /**
     * Gets the first argument of the argument which is the ID of the message or User
     */
    fun getId(args: String): String {
        return args.substringBefore(" ")
    }

    /**
     * Extracts a message from the arguments after the ID
     */
    fun extractMessage(channel: String, args: String): String {
        return args.substringAfter(channel).trim()
    }

    /**
     * Validates that a message is not empty
     */
    fun validateContent(content: String): Boolean {
        return content.isNotBlank()
    }

    override suspend fun execute(args: String, bot: BotMain): Status {
        // method should not be run directly
        return Status.FAILURE
    }
}