package reddit

import logger
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RedditTokenCreator(
    private val clientId: String,
    private val clientSecret: String,
    private val tokenCacheFile: File
) {
    private val log = logger(this::class)

    fun getAccessToken(): String {
        // if file last modified is less than 12 hours old use that token
        if (tokenCacheFile.exists() && System.currentTimeMillis() - tokenCacheFile.lastModified() < 1000 * 60 * 60 * 12) {
            log.info("Retrieving Reddit access token from local cache")
            val cachedToken = tokenCacheFile.readText()
            if (cachedToken.isNotEmpty()) {
                return cachedToken
            }
        }

        log.info("No valid cached Reddit access token found, creating new token.")
        val token = createAccessToken()
        tokenCacheFile.writeText(token)
        return token
    }

    /**
     * Creates a new access token from the Reddit API
     */
    private fun createAccessToken(): String {
        log.info("Retrieving access token from Reddit API")
        val connection = try {
            with(URL("https://www.reddit.com/api/v1/access_token").openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                setRequestProperty(
                    "Authorization",
                    "Basic ${Base64.getEncoder().encodeToString(("$clientId:$clientSecret").toByteArray())}"
                )
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                doOutput = true
                outputStream.write("grant_type=client_credentials".toByteArray())
                this
            }
        } catch (e: Exception) {
            log.error("Failed to obtain access token", e)
            throw RuntimeException("Failed to obtain access token", e)
        }

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use(BufferedReader::readText)

            val jsonResponse = JSONParser().parse(response) as JSONObject
            val token = jsonResponse["access_token"] as String

            log.info("Successfully retrieved access token from Reddit API.")
            return token
        }

        throw RuntimeException("Failed to retrieve access token from Reddit API. Response code: ${connection.responseCode}")
    }
}