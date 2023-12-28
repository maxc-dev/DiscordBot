package model

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(val alias: String, val id: String)