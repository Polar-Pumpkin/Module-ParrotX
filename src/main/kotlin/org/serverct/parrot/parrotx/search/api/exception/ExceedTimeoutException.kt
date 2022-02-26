package org.serverct.parrot.parrotx.search.api.exception

class ExceedTimeoutException(timeout: Int) : RuntimeException("Exceed timeout limit: ${timeout}s")