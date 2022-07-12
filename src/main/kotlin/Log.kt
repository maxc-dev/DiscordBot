import java.util.logging.Logger

// Return logger for Java class, if companion object fix the name
fun <T: Any> logger(forClass: Class<T>): Logger {
    return Logger.getLogger(forClass.name)
}
