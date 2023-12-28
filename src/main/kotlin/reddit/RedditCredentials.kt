package reddit

import kotlinx.serialization.Serializable

@Serializable
class RedditCredentials(val clientId: String, val clientSecret: String)