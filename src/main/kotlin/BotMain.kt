import co.touchlab.stately.concurrency.AtomicBoolean
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.ALL
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import reddit.RedditCommandManager
import io.UnknownCommand
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Channels
import model.Emojis

@OptIn(PrivilegedIntent::class)
class BotMain(
    private val discordToken: String,
    private val redditCommandManager: RedditCommandManager,
    private val channels: Channels,
    private val emojis: Emojis
) {
    private val log = logger(this::class)

    private lateinit var kord: Kord

    private val auto = AtomicBoolean(false)

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun start() {
        log.info("Starting Kord bot...")
        kord = Kord(discordToken)

        kord.on<MessageCreateEvent> {
            if (message.content.startsWith("!") && message.author?.isBot != true) {
                if (!channels.isBotValid(message.channel.id.value.toLong())) {
                    return@on
                }

                // display commands available
                if (message.content == "!help") {
                    message.channel.createMessage(redditCommandManager.getHelp())
                    return@on
                }

                // toggle auto post
                if (message.content == "!auto") {
                    auto.value = !auto.value
                    log.info("Auto post value: ${auto.value}")

                    GlobalScope.launch {
                        while (auto.value) {
                            val command = redditCommandManager.getAvailableCommands().random()
                            val content = redditCommandManager.retrieve(command)
                            if (content != null) {
                                log.info("Sending auto content for: $command")
                                message.channel.createMessage("!$command: $content")
                            }
                            delay(1000 * 30) // delay 20 seconds
                        }
                    }
                    message.channel.createMessage(
                        "Auto post has been ${
                            if (auto.value) "enabled ${emojis.getEmoji("bmt")}" 
                            else "disabled ${emojis.getEmoji("painge")}"
                        }"
                    )
                    message.delete()
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
}