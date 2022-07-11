package input

enum class Status(val showHelp: Boolean = false) {
    SUCCESS, // command was a success
    FAILURE(true), // when a command has failed
    UNKNOWN(true), // when a command is unknown
    CLOSE; // closes the program

    fun statement(): String = "Command status: [${this.name}]"
}