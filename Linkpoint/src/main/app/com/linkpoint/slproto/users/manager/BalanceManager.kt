package com.linkpoint.slproto.users.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random

/**
 * Tracks Linden Dollar balances for the currently logged in avatar, offering a
 * reactive stream for UI layers and a simple transaction history cache for the
 * finance screens. The implementation intentionally avoids any network stack
 * dependencies so it can be used inside tests and hooked up to the modern
 * protocol layer later.
 */
class BalanceManager(
    private val dataSource: BalanceDataSource = SimulatedBalanceDataSource(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val cachedSnapshot = AtomicReference<BalanceSnapshot?>(null)
    private val listeners = CopyOnWriteArraySet<(BalanceSnapshot) -> Unit>()
    private val transactions = CopyOnWriteArrayList<TransactionRecord>()
    private val balanceState = MutableStateFlow<BalanceState>(BalanceState.Stale)

    fun observeState(): StateFlow<BalanceState> = balanceState

    fun addListener(listener: (BalanceSnapshot) -> Unit) {
        cachedSnapshot.get()?.let(listener)
        listeners.add(listener)
    }

    fun removeListener(listener: (BalanceSnapshot) -> Unit) {
        listeners.remove(listener)
    }

    fun getSnapshot(): BalanceSnapshot? = cachedSnapshot.get()

    fun getTransactionHistory(limit: Int = DEFAULT_HISTORY_LIMIT): List<TransactionRecord> {
        val items = transactions.toList()
        return if (items.size <= limit) items else items.takeLast(limit)
    }

    fun recordTransaction(record: TransactionRecord) {
        transactions += record
        val current = cachedSnapshot.get()
        if (current != null) {
            val newAmount = current.amount + record.deltaLinden
            val updated = current.copy(amount = newAmount, updatedAt = System.currentTimeMillis())
            cachedSnapshot.set(updated)
            balanceState.value = BalanceState.Fresh(updated)
            notifyListeners(updated)
        }
    }

    fun refreshBalance(avatarId: UUID) {
        scope.launch {
            balanceState.emit(BalanceState.Loading)
            runCatching { dataSource.fetchBalance(avatarId) }
                .onSuccess { snapshot ->
                    cachedSnapshot.set(snapshot)
                    balanceState.emit(BalanceState.Fresh(snapshot))
                    notifyListeners(snapshot)
                }
                .onFailure { error ->
                    balanceState.emit(BalanceState.Error(error))
                }
        }
    }

    private fun notifyListeners(snapshot: BalanceSnapshot) {
        listeners.forEach { listener ->
            runCatching { listener(snapshot) }
        }
    }

    interface BalanceDataSource {
        suspend fun fetchBalance(avatarId: UUID): BalanceSnapshot
    }

    data class BalanceSnapshot(
        val avatarId: UUID,
        val amount: Long,
        val updatedAt: Long
    )

    data class TransactionRecord(
        val transactionId: UUID = UUID.randomUUID(),
        val avatarId: UUID,
        val description: String,
        val deltaLinden: Long,
        val counterparty: String?,
        val timestamp: Long = System.currentTimeMillis()
    )

    sealed class BalanceState {
        object Stale : BalanceState()
        object Loading : BalanceState()
        data class Fresh(val snapshot: BalanceSnapshot) : BalanceState()
        data class Error(val throwable: Throwable) : BalanceState()
    }

    private class SimulatedBalanceDataSource : BalanceDataSource {
        override suspend fun fetchBalance(avatarId: UUID): BalanceSnapshot = withContext(Dispatchers.Default) {
            // Simulate I/O delay
            kotlinx.coroutines.delay(150)
            BalanceSnapshot(
                avatarId = avatarId,
                amount = Random.nextLong(50_000, 250_000),
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    companion object {
        private const val DEFAULT_HISTORY_LIMIT = 50
    }
}
