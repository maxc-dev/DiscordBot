package io

class UnknownCommand(command: String, user: String? = null) :
    IllegalArgumentException("Unknown command${if (user != null) " from user [$user]" else ""}: $command")