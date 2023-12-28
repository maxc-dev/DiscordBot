package reddit

import logger
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RedditTokenCreator(private val clientId: String, private val clientSecret: String, private val tokenCacheFile: File) {
    private val log = logger(this::class)

    fun getAccessToken(): String {
        if (tokenCacheFile.exists()) {
            val cachedToken = tokenCacheFile.readText()
            log.info("Retrieving Reddit access token from local cache")
            if (cachedToken.isNotEmpty()) {
                return cachedToken
            }
        }
        log.info("Retrieving Reddit access token from Reddit API")
        return createAccessToken()
    }

    private fun createAccessToken(): String {
        log.info("Retrieving access token")
        val authString = "$clientId:$clientSecret"
        val base64Auth: String = Base64.getEncoder().encodeToString(authString.toByteArray())

        try {
            val tokenUrl = URL("https://www.reddit.com/api/v1/access_token")
            val connection = tokenUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Basic $base64Auth")
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true

            val requestBody = "grant_type=client_credentials"
            connection.outputStream.write(requestBody.toByteArray())

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = java.lang.StringBuilder()
                var inputLine: String?

                while ((reader.readLine().also { inputLine = it }) != null) {
                    response.append(inputLine)
                }
                reader.close()

                val parser = JSONParser()
                val jsonResponse = parser.parse(response.toString()) as JSONObject
                val token = jsonResponse["access_token"] as String

                // write token to file
                tokenCacheFile.writeText(token)
                log.info("Successfully retrieved access token and updated local cache")
                return token
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        log.warn("Could not retrieve access token from Reddit API. Attempting to read from local cache...")
        if (tokenCacheFile.exists()) {
            log.info("Successfully retrieved access token from local cache")
            return tokenCacheFile.readText()
        }

        throw RuntimeException("Failed to obtain access token")
    }
}