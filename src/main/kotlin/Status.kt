enum class Status(val showHelp: Boolean = false, private val desc: String = "") {
    SUCCESS, // command was a success
    FAILURE(true), // when a command has failed
    UNKNOWN_COMMAND(true, "The initial command (first argument) is invalid"), // when a command is unknown
    UNKNOWN_ARGUMENT(desc = "The argument after the command is invalid"), // when a command's argument is unknown
    CLOSE; // closes the program

    override fun toString(): String {
        return "Command status: [${this.name.replace("_", " ")}]${if (desc.isNotEmpty()) " -> $desc" else ""}"
    }

    fun statement(): String = toString()
}