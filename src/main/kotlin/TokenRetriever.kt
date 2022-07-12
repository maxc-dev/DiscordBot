import javax.swing.filechooser.FileSystemView
import kotlin.io.path.exists
import kotlin.io.path.readText

const val TOKEN_FILE_NAME = "token.txt"

class TokenRetriever {
    private val log = logger(this.javaClass)

    fun retrieveToken(): String {
        val path = FileSystemView.getFileSystemView().defaultDirectory.toPath()
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