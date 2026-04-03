package com.example.sharedkernel.errorhandling

import jdk.internal.org.jline.reader.impl.InputRC.configure
import kotlin.reflect.KClass


class ExceptionMapperRegistry(configure: ExceptionMapperRegistry.() -> Unit = {}) {
    /**
     * Type alias for a function that maps any [Throwable] to a [ProblemJsonException].
     */
    private typealias ExceptionMapper = (Throwable) -> ProblemJsonException

    private val mappers = mutableMapOf<KClass<out Throwable>, ExceptionMapper>()

    init {
        configure()
    }

    /**
     * Registers a mapper for a specific exception type [T].
     */
    fun <T : Throwable> register(type: KClass<T>, mapper: (T) -> ProblemJsonException) {
        @Suppress("UNCHECKED_CAST")
        mappers[type] = mapper as ExceptionMapper
    }

    /**
     * Inline convenience overload allowing type-safe registration without explicit KClass:
     * ```
     * registry.register<IllegalArgumentException> { ex -> ... }
     * ```
     */
    inline fun <reified T : Throwable> register(noinline mapper: (T) -> ProblemJsonException) {
        register(T::class, mapper)
    }

    /**
     * Resolves [exception] to a [ProblemJsonException] by walking the exception's class
     * hierarchy until a registered mapper is found.
     *
     * Resolution order:
     * 1. Exact class match
     * 2. Nearest superclass match (closest in the MRO)
     * 3. [fallback] if no match is found (defaults to a generic 500 response)
     */
    fun resolve(
        exception: Throwable,
    ): ProblemJsonException? {
        var klass: Class<*>? = exception::class.java
        while (klass != null) {
            val mapper = mappers[klass.kotlin]
            if (mapper != null) return mapper(exception)
            klass = klass.superclass
        }

        return null
    }

    /**
     * Returns a read-only snapshot of all registered mappings.
     */
    fun registeredTypes(): Set<KClass<out Throwable>> = mappers.keys.toSet()
}