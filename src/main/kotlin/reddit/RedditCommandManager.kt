package reddit

import io.UnknownCommand
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import logger
import java.io.File


class RedditCommandManager(private val redditCommandMap: RedditCommandMap, redditCredentials: RedditCredentials, redditTokenFile: File) {
    private val log = logger(this::class)

    private val redditTokenManager = RedditTokenCreator(redditCredentials.clientId, redditCredentials.clientSecret, redditTokenFile)
    private val redditToken = redditTokenManager.getAccessToken()


    // maps a command name to a list of content URLs
    private val redditClient = redditCommandMap.commands.shuffled().associateWith { RedditClient(redditCommandMap.getSubFromCommand(it), redditToken) }

    // map of command name to latest refresh timestamp
    private val refreshCache = HashMap<String, Long>()

    init {
        log.info("Initialized Command Manager with ${redditCommandMap.commands.size} commands: ${redditCommandMap.commands.joinToString { it }}")
        // initialize refresh cache
        redditCommandMap.commands.forEach {
            refreshCache[it] = System.currentTimeMillis()
        }

        log.info("Initializing automatic cache refresh...")
        refreshCache()
    }

    fun getHelp(): String {
        return "Available commands (queue/cache): ${redditCommandMap.commands.joinToString { "!$it (${redditClient[it]?.getQueueSize()}/${redditClient[it]?.getCacheSize()})" }}"
    }

    fun getAvailableCommands(): List<String> {
        return redditCommandMap.commands.filter { (redditClient[it]?.getQueueSize() ?: 0) > 0 }
    }

    /**
     * Periodically refreshes the cache of posts for each command
     * Prioritizing commands with the least recently refreshed cache
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun refreshCache() {
        GlobalScope.launch {
            while (true) {
                val cmd = refreshCache.minByOrNull { it.value }!!.key
                if (redditClient[cmd]!!.refreshCache()) {
                    refreshCache[cmd] = System.currentTimeMillis()
                }
                delay(1000 * 15) // 15 seconds
            }
        }
    }

    fun retrieve(src: String): String? {
        val cmd = src.removePrefix("!")
        if (redditCommandMap.commands.contains(cmd)) {
            return redditClient[cmd]!!.getNextPostUrl()
        }

        throw UnknownCommand(cmd)
    }

}