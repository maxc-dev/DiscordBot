import java.io.File

class TokenRetriever {
    private val log = logger(this.javaClass)

    /**
     * Retrieves the token from the token file.
     */
    fun retrieveToken(file: File): String {
        log.info("Retrieving token from: $file")

        if (file.exists()) {
            return file.readText()
        }
        log.severe("Token file not found")
        throw RuntimeException("Token file not found")
    }
}