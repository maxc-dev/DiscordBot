import java.nio.file.Paths
import javax.swing.filechooser.FileSystemView
import kotlin.io.path.exists
import kotlin.io.path.readText

const val TOKEN_FOLDER = "discord-bot/"
const val TOKEN_FILE_NAME = "token.txt"

class TokenRetriever {
    private val log = logger(this.javaClass)

    /**
     * Retrieves the token from the token file.
     */
    fun retrieveToken(): String {
        val path = Paths.get(FileSystemView.getFileSystemView().defaultDirectory.path, TOKEN_FOLDER)
        log.info("Retrieving token from: $path")

        if (path.exists()) {
            val file = path.resolve(TOKEN_FILE_NAME)
            if (file.exists()) {
                return file.readText()
            }
        }
        log.severe("Token file not found")
        throw RuntimeException("Token file not found")
    }
}