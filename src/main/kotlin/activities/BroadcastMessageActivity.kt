package activities

import BotMain
import Status
import dev.kord.common.entity.Snowflake
import logger

class BroadcastMessageActivity : MessageDistributorActivity() {
    private val log = logger(this.javaClass)
    /**
     * Broadcasts a message to a channel.
     */
    override suspend fun execute(args: String, bot: BotMain): Status {
        val channelName = getId(args)
        val channelId = getChannelId(channelName) ?: return Status.UNKNOWN_ARGUMENT

        val message = extractMessage(channelName, args)
        if (!validateContent(message)) return Status.UNKNOWN_ARGUMENT

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
}