package reddit

import logger
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class RedditClient(private val subreddit: String, private val accessToken: String) {
    private val log = logger(this::class)

    private val url = "https://oauth.reddit.com/r/$subreddit/top/.json"

    // cache of post url to timestamp
    private val cache = java.util.concurrent.ConcurrentHashMap<String, Long>()

    // priority queue of post urls by largest timestamp
    private val queue = PriorityQueue<String>(100) { a, b -> (cache[b] ?: 0).compareTo(cache[a] ?: 0) }

    /**
     * Refreshes the cache of top posts
     */
    fun refreshCache(): Boolean {
        log.info("Attempting to refresh cache for $subreddit")
        try {
            val postUrls = getTopPostUrls(url)
            if (postUrls.isEmpty()) {
                log.info("No posts retrieved from $subreddit")
                return false
            }

            log.info("Retrieved ${postUrls.size} posts from $subreddit")
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
                log.info("Queue is empty. Filling with cached posts for $subreddit")
                queue.addAll(cache.keys)
            }

            log.info("Cached $count posts from $subreddit, total cached: ${cache.size}, total in queue: ${queue.size}")
            return true
        } catch (e: Exception) {
            log.error("Error while getting posts for $url", e)
        }
        log.error("Failed to refresh cache for $subreddit")
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

    private fun getConnection(url: String) = (URL(url).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        setRequestProperty("Authorization", "bearer $accessToken")
    }

    @Throws(Exception::class)
    private fun getTopPostUrls(url: String): LinkedList<String> {
        val connection = getConnection(url)

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            log.warn("Failed to retrieve top posts from $url, response: ${connection.responseCode} ${connection.responseMessage}")
            return LinkedList()
        }

        val content = connection.inputStream.bufferedReader().use(BufferedReader::readText)

        // Parse JSON response
        val parsed: JSONObject = JSONParser().parse(content) as JSONObject
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
}
