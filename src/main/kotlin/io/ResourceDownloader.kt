package io

import com.github.jreddit.entity.Submission
import com.github.jreddit.entity.User
import com.github.jreddit.retrieval.Submissions
import com.github.jreddit.retrieval.params.SubmissionSort
import com.github.jreddit.utils.restclient.PoliteHttpRestClient
import logger

class ResourceDownloader(private val subreddit: String, private val creds: Credentials) {
    private val log = logger(this.javaClass)

    private val restClient = PoliteHttpRestClient().apply { this.setUserAgent(creds.agent) }
    private val user = User(restClient, creds.username, creds.pw)

    init {
        log.info("Connecting user")
        user.connect()
        log.info("User connected")
    }

    fun acquire(): List<Submission> {
        log.info("Acquiring submissions from $subreddit")
        val subs = Submissions(restClient, user)
        val submissions = subs.ofSubreddit(subreddit, SubmissionSort.HOT, -1, CommandManager.CACHE_SIZE, null, null, true)
        log.info("Acquired ${submissions.size} entries from $subreddit")
        return submissions
    }
}