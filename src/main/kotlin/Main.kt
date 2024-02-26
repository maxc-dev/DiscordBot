import config.BotConfig
import reddit.RedditCommandManager
import kotlinx.coroutines.runBlocking

/**
 * @param args Argument pattern:
 *  - config file
 */
fun main(args: Array<String>) {
    if (args.size != 1) {
        throw IllegalArgumentException("Config file argument required only.")
    }
    Launcher().start(args)
}

class Launcher {
    private val log = logger(this::class)

    fun start(args: Array<String>) = runBlocking {
        log.info("Discord Bot initialization started")

        val config = BotConfig(args[0])
        val redditCredentials = config.getRedditCredentials()
        val redditTokenCache = config.getRedditTokenCacheFile()
        val redditCommandMap = config.getRedditCommandMap()
        val redditCommandManager = RedditCommandManager(redditCommandMap, redditCredentials, redditTokenCache)

        val discordToken = config.getDiscordSecret()
        val channels = config.getChannels()
        val emojis = config.getEmojis()

        val discordCommandInput = DiscordCommandInput(discordToken, redditCommandManager, channels, emojis)
        discordCommandInput.start()
    }
}
