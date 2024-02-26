import co.touchlab.stately.concurrency.AtomicBoolean
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import reddit.RedditCommandManager
import io.UnknownCommand
import io.ktor.util.collections.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Channels
import model.Emojis

@OptIn(PrivilegedIntent::class)
class DiscordCommandInput(
    private val discordToken: String,
    private val redditCommandManager: RedditCommandManager,
    private val channels: Channels,
    private val emojis: Emojis
) {
    private val log = logger(this::class)

    private lateinit var kord: Kord

    private var autoContent = ConcurrentSet<String>()
    private var randomAutoContent = AtomicBoolean(false)

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun start() {
        log.info("Starting Kord bot...")
        kord = Kord(discordToken)

        kord.on<MessageCreateEvent> {
            if (!message.content.startsWith("!") || message.author?.isBot!! || !channels.isBotValid(message.channel.id.value.toLong())) {
                return@on
            }

            // display commands available
            if (message.content == "!help") {
                message.channel.createMessage(redditCommandManager.getHelp())
                message.channel.createMessage(
                    "Auto post is currently ${if (isAutoContentEnabled()) "enabled" else "disabled"}\n" +
                            "To enable auto post, type `!auto x,y,z` where `x,y,z` are the commands you want to enable." +
                            "\nTo disable auto post, type `!auto`"
                )
                message.delete()
                return@on
            }

            // toggle auto post
            if (message.content.startsWith("!auto")) {
                if (isAutoContentEnabled()) { // means that auto content is enabled and needs to be toggled off
                    log.info("Auto post disabled for ${getAutoString()}")
                    message.channel.createMessage("Auto post has been disabled for ${getAutoString()}.")
                    autoContent.clear()
                    randomAutoContent.value = false
                    message.delete()
                    return@on
                }

                // find commands after !auto and add them to autoContent
                val commands = message.content.substringAfter("!auto").trim().split(",")
                    .filter { it.isNotBlank() && redditCommandManager.getAllCommands().contains(it) }

                // if no commands are specified, enable auto post for all commands
                randomAutoContent.value = commands.isEmpty()
                autoContent.addAll(commands)

                log.info("Auto post enabled for ${getAutoString()}")
                message.channel.createMessage("Auto post has been enabled for ${getAutoString()}")
                message.delete()

                GlobalScope.launch {
                    while (isAutoContentEnabled()) {
                        val command = getAutoContents().randomOrNull() ?: continue
                        val content = redditCommandManager.retrieve(command)
                        if (content != null) {
                            log.info("Sending auto content for: $command")
                            message.channel.createMessage("!$command: $content")
                        }
                        delay(1000 * 30) // delay 20 seconds
                    }
                    return@launch
                }
                return@on
            }

            try {
                log.info("User ${message.author?.data?.username} requested content for ${message.content}")
                val content = redditCommandManager.retrieve(message.content)
                if (content != null) {
                    message.channel.createMessage("${message.content}: $content")
                } else {
                    message.channel.createMessage(
                        "Reddit API is being slow... try one of these instead: ${
                            redditCommandManager.getAvailableCommands().joinToString { "!$it" }
                        }"
                    )
                }
                message.delete()
            } catch (e: UnknownCommand) {
                log.warn(e.localizedMessage)
            }

            return@on
        }

        kord.login {
            presence {
                playing("!help")
            }

            @OptIn(PrivilegedIntent::class)
            intents = Intents.ALL
        }
    }

    suspend fun stop() {
        log.info("Stopping bot...")
        kord.shutdown()
    }

    private fun isAutoContentEnabled() = autoContent.isNotEmpty() || randomAutoContent.value

    private fun getAutoContents() =
        if (randomAutoContent.value) redditCommandManager.getAvailableCommands() else autoContent

    private fun getAutoString() = if (randomAutoContent.value) "all content" else autoContent.joinToString()
}