package io

import kotlinx.serialization.Serializable

@Serializable
class Credentials(val agent: String, val username: String, val pw: String)