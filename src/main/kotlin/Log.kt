import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

val logger: (KClass<*>) -> Logger = { LoggerFactory.getLogger(it.java.name) }