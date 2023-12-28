package reddit

import logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class RedditClient(subreddit: String, private val accessToken: String) {
    private val log = logger(this::class)

    private val url = "https://oauth.reddit.com/r/$subreddit/top/.json"

    // cache of post url to timestamp
    private val cache = java.util.concurrent.ConcurrentHashMap<String, Long>()

    // priority queue of post urls by largest timestamp
    private val queue = PriorityQueue<String>(100) { a, b -> (cache[b] ?: 0).compareTo(cache[a] ?: 0)}

    /**
     * Refreshes the cache of top posts
     */
    fun refreshCache(): Boolean {
        log.info("Attempting to refresh cache for $url")
        try {
            val postUrls = getTopPostUrls(url)
            if (postUrls.isEmpty()) {
                log.info("No posts retrieved from $url")
                return false
            }

            log.info("Retrieved ${postUrls.size} posts from $url")
            var count = 0

            // if post is in queue or cache, ignore it
            postUrls.forEach {
                if (!cache.contains(it)) {
                    cache[it] = System.currentTimeMillis()
                    count++
                }
            }

            // if queue is empty then fill it with all the cached posts
            if (queue.isEmpty()) {
                log.info("Queue is empty. Filling with cached posts for $url")
                queue.addAll(cache.keys)
            }

            log.info("Cached $count posts from $url, total cached: ${cache.size}, total in queue: ${queue.size}")
            return true
        } catch (e: Exception) {
            log.error("Error while getting posts for $url", e)
        }
        log.warn("Failed to refresh cache for $url")
        return false
    }

    /**
     * Gets the next post url in the cache
     */
    fun getNextPostUrl(): String? {
        if (queue.isEmpty()) {
            log.info("Cache is empty. Refreshing...")
            if (!refreshCache()) {
                log.warn("Failed to directly refresh cache for $url")
                return null
            }
        }
        return queue.poll()
    }

    fun getCacheSize() = cache.size
    fun getQueueSize() = queue.size

    private fun getConnectedUrl(url: String): HttpURLConnection {
        val redditUrl = URL(url)
        val connection = redditUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "bearer $accessToken")
        return connection
    }

    @Throws(Exception::class)
    private fun getTopPostUrls(url: String): LinkedList<String> {
        val connection = getConnectedUrl(url)
        val responseCode = connection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var inputLine: String?
            val content = StringBuilder()

            while ((reader.readLine().also { inputLine = it }) != null) {
                content.append(inputLine)
            }

            reader.close()

            // Parse JSON response
            val parser = JSONParser()
            val parsed: JSONObject = parser.parse(content.toString()) as JSONObject

            val data = parsed["data"] as JSONObject
            val children = data["children"] as JSONArray

            val postUrls = LinkedList<String>()
            for (i in 0 until children.size) {
                val post: JSONObject = children[i] as JSONObject
                val data1 = post["data"] as JSONObject
                val postUrl = data1["url"] as String
                postUrls.add(postUrl)
            }

            return postUrls
        }
        log.warn("Failed to retrieve top posts from $url")
        return LinkedList()
    }
}
