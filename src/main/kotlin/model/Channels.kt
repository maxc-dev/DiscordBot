package model

import kotlinx.serialization.Serializable

@Serializable
class Channels(private val channels: List<Long>) {
    fun isBotValid(channelId: Long) = channels.contains(channelId)
}