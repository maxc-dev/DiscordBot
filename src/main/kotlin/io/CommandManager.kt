package io

import com.github.jreddit.entity.Submission
import logger
import java.util.concurrent.atomic.AtomicInteger

class CommandManager(private val commandMap: CommandMap, private val credentials: Credentials) {
    private val log = logger(this.javaClass)

    // maps a command name to a list of content URLs
    private val downloader = commandMap.commands.associateWith { ResourceDownloader(commandMap.getSubFromCommand(it), credentials) }
    private val cache = HashMap<String, List<Submission>>()
    private val index = HashMap<String, AtomicInteger>()

    init {
        log.info("Initializing cache...")
        commandMap.commands.forEach { replenishCache(it) }
        log.info("Cache initialized")
    }

    fun retrieve(src: String): Submission {
        val cmd = src.removePrefix("!")
        if (commandMap.commands.contains(cmd)) {
            // replenish cache if indexed all in cache
            if (index.getOrDefault(cmd, AtomicInteger(1)).get() >= CACHE_SIZE) {
                replenishCache(cmd)
            }
            return cache[cmd]?.get(index[cmd]!!.getAndIncrement() % CACHE_SIZE) ?: throw UnknownCommand(cmd)
        }

        throw UnknownCommand(cmd)
    }

    private fun replenishCache(cmd: String) {
        log.info("Replenishing cache for $cmd")
        cache[cmd] = downloader[cmd]?.acquire()?.toMutableList() ?: throw UnknownCommand(cmd)
        index[cmd] = AtomicInteger(0)
    }

    companion object {
        const val CACHE_SIZE = 100
    }
}