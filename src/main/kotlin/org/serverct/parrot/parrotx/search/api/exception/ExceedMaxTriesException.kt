package org.serverct.parrot.parrotx.search.api.exception

class ExceedMaxTriesException(maxTries: Int) : RuntimeException("Exceed max tries limit: $maxTries")