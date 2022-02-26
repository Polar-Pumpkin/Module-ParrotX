package org.serverct.parrot.parrotx.search.api.exception

import org.serverct.parrot.parrotx.search.api.filter.Filter

class ValidateFailureException(filter: Filter<*>, cause: Throwable) : RuntimeException("Value validation failed: ${filter::class.simpleName}", cause)