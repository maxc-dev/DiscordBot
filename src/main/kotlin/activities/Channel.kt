package activities

private enum class Channel(val id: Long) {
    GENERAL(613694064956669953),
    ADMIN(695279845533286490),
    TEST(614556024518475786)
}

/**
 * Wrap enum in map to make channel lookups constant time
 */
object ChannelManager {
    val channelMapper = Channel.values().associate { it.name to it.id }.toMap()
}