package com.linkpoint.slproto.users.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Provides people/place/event search functionality built on top of a simple
 * in-memory cache. The real protocol transport can replace the simulated
 * provider without changing callers.
 */
class SearchManager(
    private val provider: SearchProvider = SimulatedSearchProvider(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val cache = ConcurrentHashMap<SearchRequest, List<SearchResult>>()
    private val stateFlow = MutableStateFlow<SearchState>(SearchState.Idle)

    fun observeState(): StateFlow<SearchState> = stateFlow

    fun clearCache() = cache.clear()

    fun search(request: SearchRequest) {
        scope.launch {
            stateFlow.emit(SearchState.Loading(request))
            val cached = cache[request]
            if (cached != null) {
                stateFlow.emit(SearchState.Results(request, cached, fromCache = true))
                return@launch
            }

            runCatching {
                provider.search(request)
            }.onSuccess { results ->
                cache[request] = results
                stateFlow.emit(SearchState.Results(request, results, fromCache = false))
            }.onFailure { error ->
                stateFlow.emit(SearchState.Error(request, error))
            }
        }
    }

    data class SearchRequest(
        val domain: SearchDomain,
        val query: String,
        val filters: SearchFilters = SearchFilters()
    )

    data class SearchFilters(
        val maturity: Maturity = Maturity.MODERATE,
        val limit: Int = 25,
        val sortOrder: SortOrder = SortOrder.RELEVANCE,
        val onlyOnline: Boolean = false
    )

    enum class SearchDomain { PEOPLE, PLACES, EVENTS }
    enum class Maturity { GENERAL, MODERATE, ADULT }
    enum class SortOrder { RELEVANCE, NAME, POPULARITY, RECENCY }

    data class SearchResult(
        val id: UUID,
        val name: String,
        val description: String,
        val tags: List<String>,
        val online: Boolean = false,
        val population: Int = 0,
        val eventStart: Long? = null
    )

    sealed class SearchState {
        object Idle : SearchState()
        data class Loading(val request: SearchRequest) : SearchState()
        data class Results(
            val request: SearchRequest,
            val results: List<SearchResult>,
            val fromCache: Boolean
        ) : SearchState()
        data class Error(val request: SearchRequest, val throwable: Throwable) : SearchState()
    }

    interface SearchProvider {
        suspend fun search(request: SearchRequest): List<SearchResult>
    }

    private class SimulatedSearchProvider : SearchProvider {
        override suspend fun search(request: SearchRequest): List<SearchResult> {
            // Pretend each domain is a different network call
            return when (request.domain) {
                SearchDomain.PEOPLE -> generateResults("Resident", onlineFlag = true, filters = request.filters)
                SearchDomain.PLACES -> generateResults("Region", population = true, filters = request.filters)
                SearchDomain.EVENTS -> generateResults("Event", event = true, filters = request.filters)
            }
        }

        private suspend fun generateResults(
            baseName: String,
            onlineFlag: Boolean = false,
            population: Boolean = false,
            event: Boolean = false,
            filters: SearchFilters
        ): List<SearchResult> {
            val count = filters.limit.coerceIn(1, MAX_SIM_RESULTS)
            val tasks = (0 until count).map { index ->
                kotlinx.coroutines.delay(5) // simulate pagination
                SearchResult(
                    id = UUID.randomUUID(),
                    name = "$baseName ${index + 1}",
                    description = "Auto-generated result matching query",
                    tags = listOf("modern", "linkpoint"),
                    online = if (onlineFlag) Random.nextBoolean() else false,
                    population = if (population) Random.nextInt(0, 150) else 0,
                    eventStart = if (event) System.currentTimeMillis() + Duration.ofHours(index.toLong()).toMillis() else null
                )
            }
            return tasks
        }
    }

    companion object {
        private const val MAX_SIM_RESULTS = 50
    }
}
