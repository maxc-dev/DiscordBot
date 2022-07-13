package activities

import BotMain
import Status
import dev.kord.common.entity.Snowflake
import logger

class BroadcastMessageActivity : MessageDistributorActivity() {
    private val log = logger(this.javaClass)

    val TAG = "@"

    /**
     * Broadcasts a message to a channel.
     */
    override suspend fun execute(args: String, bot: BotMain): Status {
        val channelName = getId(args)
        val channelId = getChannelId(channelName) ?: return Status.UNKNOWN_ARGUMENT

        var message = extractMessage(channelName, args)
        if (!validateContent(message)) return Status.UNKNOWN_ARGUMENT
        if (message.contains(TAG)) {
            message = insertTags(message)
        }

        val snowflake = Snowflake(channelId)
        bot.rest.channel.createMessage(snowflake) {
            content = message
        }
        log.info("Sent message to channel $channelName: $message")
        return Status.SUCCESS
    }

    private fun getChannelId(channelId: String): Long? {
        return ChannelManager.channelMapper[channelId.uppercase()]
    }

    private fun insertTags(message: String): String {
        var taggedMessage = message
        Role.values().forEach {
            taggedMessage = taggedMessage.replace(TAG + it.name, it.tag(), true)
        }
        return taggedMessage
    }
}