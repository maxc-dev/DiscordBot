package model

import io.UnknownEmoji
import kotlinx.serialization.Serializable

@Serializable
@Suppress("CanBeParameter")
class Emojis(private val emojis: List<Emoji>) {
    private val mapAliasToEmojiId = emojis.associateBy({ it.alias }, { it.id })
    private val emojiAliases = emojis.map { it.alias }

    fun getEmoji(alias: String): String {
        return mapAliasToEmojiId[alias] ?: throw UnknownEmoji(alias, emojiAliases)
    }
}