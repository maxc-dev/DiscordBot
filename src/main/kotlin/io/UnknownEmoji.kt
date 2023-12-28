package io

class UnknownEmoji(emojiAlias: String, legalEmojis: List<String>? = null) :
    IllegalArgumentException("Unknown emoji alias: $emojiAlias" + if (legalEmojis != null) " (legal aliases: $legalEmojis.joinToString())" else "")