package activities

import BotMain
import Status
import dev.kord.common.entity.Snowflake

class SendMessageActivity(bot: BotMain) : MessageDistributorActivity(bot) {
    /**
     * Sends a message to a channel.
     */
    override suspend fun execute(args: String): Status {
        val channelId = getId(args)
        val snowflake = Snowflake(channelId)
        val message = kord.getChannel(snowflake)
        //todo figure out the kord api...
        return Status.SUCCESS
    }
}