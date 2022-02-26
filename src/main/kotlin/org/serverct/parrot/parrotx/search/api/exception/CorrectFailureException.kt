package org.serverct.parrot.parrotx.search.api.exception

import org.serverct.parrot.parrotx.search.api.corrector.Corrector

class CorrectFailureException(corrector: Corrector<*>, cause: Throwable) : RuntimeException("Value correction failed: ${corrector::class.simpleName}", cause)