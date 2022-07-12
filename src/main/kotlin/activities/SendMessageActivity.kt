package activities

import BotMain
import Status
import dev.kord.common.entity.Snowflake
import logger

class SendMessageActivity(bot: BotMain) : MessageDistributorActivity(bot) {
    private val log = logger(this.javaClass)
    /**
     * Sends a message to a channel.
     */
    override suspend fun execute(args: String): Status {
        val channelName = getId(args)
        val channelId = getChannelId(channelName) ?: return Status.UNKNOWN_ARGUMENT
        val message = args.substringAfter(channelName).trim()
        if (message.isEmpty()) {
            log.warning("No message to send")
            return Status.UNKNOWN_ARGUMENT
        }

        val snowflake = Snowflake(channelId)
        rest.channel.createMessage(snowflake) {
            content = message
        }
        log.info("Sent message to channel $channelName: $message")
        return Status.SUCCESS
    }

    private fun getChannelId(channelId: String): Long? {
        return ChannelManager.channelMapper[channelId.uppercase()]
    }
}