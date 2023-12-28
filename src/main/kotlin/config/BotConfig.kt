package config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import model.Channels
import model.Emojis
import reddit.RedditCommandMap
import reddit.RedditCredentials
import java.io.File
import kotlin.io.path.Path

class BotConfig(private val configFile: String) {
    private val configFiles = Json.decodeFromString<ConfigFiles>(File(configFile).readText())

    fun getRedditCommandMap() = Json.decodeFromString<RedditCommandMap>(getFileText(configFiles.userCommands))
    fun getRedditCredentials() = Json.decodeFromString<RedditCredentials>(getFileText(configFiles.redditCredentials))
    fun getDiscordSecret() = getFileText(configFiles.discordSecret)
    fun getChannels() = Json.decodeFromString<Channels>(getFileText(configFiles.channels))
    fun getEmojis() = Json.decodeFromString<Emojis>(getFileText(configFiles.emojis))
    fun getRedditTokenCacheFile() = File(Path(configFile).parent.toString() + "/" + configFiles.redditTokenCache)

    private fun getFileText(fileName: String) = File(Path(configFile).parent.toString() + "/" + fileName).readText()

    @Serializable
    data class ConfigFiles(
        val userCommands: String,
        val redditCredentials: String,
        val discordSecret: String,
        val channels: String,
        val emojis: String,
        val redditTokenCache: String)
}
