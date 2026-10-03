/**
 * Linkpoint - Modular Financial Manager
 *
 * Handles persistent transaction ledger (30-day retention), quick-tip preset calculations,
 * payment RPC dispatching for avatars & objects, background transaction reconciliation,
 * and local cache synchronization via localCache (IndexedDB STORE_TRANSACTIONS).
 */

import { Utils } from './utils';
import { localCache } from './local-cache';
import { slBridge } from './sl-bridge';

export interface TransactionRecord {
  id: string;
  agentId: string;
  targetId: string;
  targetName: string;
  targetType: 'avatar' | 'object' | 'group' | 'system';
  amount: number;
  type: 'payment' | 'tip' | 'purchase' | 'refund' | 'transfer' | 'balance';
  description: string;
  timestamp: number;
  status: 'success' | 'failed' | 'declined' | 'pending';
}

export interface PaymentParams {
  targetId: string;
  targetName?: string;
  amount: number;
  description?: string;
}

export interface EconomyStats {
  balance: number | null;
  currencySymbol?: string;
  isZeroCurrency?: boolean;
  totalTransactions: number;
  totalSpent30Days: number;
  totalReceived30Days: number;
  oldestTransactionDate: string | null;
}

export class EconomyManager extends Utils.EventEmitter {
  public balance: number | null = null;
  public currencySymbol: string = 'L$';
  public isZeroCurrency: boolean = false;
  public transactions: TransactionRecord[] = [];
  public activeAgentId: string = 'current';

  constructor() {
    super();
    this.setupListeners();
  }

  public setGridCurrency(symbol: string = 'L$', isZeroCurrency = false): void {
    this.currencySymbol = symbol || 'L$';
    this.isZeroCurrency = Boolean(isZeroCurrency);
    this.emit('currency_updated', {
      currencySymbol: this.currencySymbol,
      isZeroCurrency: this.isZeroCurrency,
    });
  }

  private setupListeners() {
    slBridge.on('balance_updated', (data: any) => {
      if (typeof data === 'number') {
        this.balance = data;
      } else if (data && typeof data === 'object') {
        if (typeof data.balance === 'number' || data.balance === null) {
          this.balance = data.balance;
        }
        if (data.currencySymbol || data.currency_symbol) {
          this.currencySymbol = data.currencySymbol || data.currency_symbol;
        }
        if (typeof data.isZeroCurrency === 'boolean' || typeof data.is_zero_currency === 'boolean') {
          this.isZeroCurrency = Boolean(data.isZeroCurrency ?? data.is_zero_currency);
        }
        if (data.transaction) {
          void this.recordTransaction(data.transaction);
        }
      } else if (data === null) {
        this.balance = null;
      }
      this.emit('balance_updated', {
        balance: this.balance,
        currencySymbol: this.currencySymbol,
        isZeroCurrency: this.isZeroCurrency,
      });
    });

    slBridge.on('transaction-recorded', (tx: any) => {
      void this.recordTransaction(tx);
    });
  }

  /**
   * Initialize EconomyManager for active agent UUID.
   * Loads 30-day cached transaction records from IndexedDB.
   */
  public async init(agentId?: string): Promise<void> {
    if (agentId) {
      this.activeAgentId = agentId;
    }

    // Prune entries older than 30 days
    await localCache.pruneOldTransactions(this.activeAgentId, 30);

    // Load cached 30-day transaction history from IndexedDB
    const cached = await localCache.getTransactions(this.activeAgentId);
    this.transactions = cached;
    this.emit('transactions_updated', this.transactions);
  }

  /**
   * Quick-tip preset amounts (standard tipping amounts).
   */
  public getQuickTipPresets(): number[] {
    return [5, 10, 50, 100];
  }

  /**
   * Send payment to an in-world Object (tip jar, vendor, rental box, etc.)
   */
  public async payObject(params: PaymentParams): Promise<TransactionRecord> {
    if (this.isZeroCurrency) {
      throw new Error('Payments are disabled on zero-currency grids');
    }
    const targetId = params.targetId;
    if (!targetId) {
      throw new Error('Target object ID is required');
    }
    const amount = Math.floor(Number(params.amount));
    if (isNaN(amount) || amount <= 0) {
      throw new Error('Payment amount must be a positive integer');
    }

    const description = params.description || `Payment to object ${params.targetName || targetId}`;
    const targetName = params.targetName || 'Object';

    try {
      const res = await slBridge.payObject({
        targetId,
        amount,
        description,
        targetName,
        currencySymbol: this.currencySymbol,
        isZeroCurrency: this.isZeroCurrency,
      });

      const record: TransactionRecord = {
        id: res?.transaction?.id || `tx_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
        agentId: this.activeAgentId,
        targetId,
        targetName,
        targetType: 'object',
        amount,
        type: 'payment',
        description,
        timestamp: Date.now(),
        status: 'success',
      };

      await this.recordTransaction(record);
      return record;
    } catch (err: any) {
      const failedRecord: TransactionRecord = {
        id: `tx_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
        agentId: this.activeAgentId,
        targetId,
        targetName,
        targetType: 'object',
        amount,
        type: 'payment',
        description: `${description} (Failed: ${err.message || 'Declined'})`,
        timestamp: Date.now(),
        status: 'failed',
      };
      await this.recordTransaction(failedRecord);
      throw new Error(err.message || 'Payment declined or failed');
    }
  }

  /**
   * Send payment/tip to another Resident/Avatar.
   */
  public async payAvatar(params: PaymentParams): Promise<TransactionRecord> {
    if (this.isZeroCurrency) {
      throw new Error('Payments are disabled on zero-currency grids');
    }
    const targetId = params.targetId;
    if (!targetId) {
      throw new Error('Target avatar ID is required');
    }
    const amount = Math.floor(Number(params.amount));
    if (isNaN(amount) || amount <= 0) {
      throw new Error('Payment amount must be a positive integer');
    }

    const description = params.description || `Gift to ${params.targetName || targetId}`;
    const targetName = params.targetName || 'Resident';

    try {
      const res = await slBridge.payAvatar({
        targetId,
        amount,
        description,
        targetName,
        currencySymbol: this.currencySymbol,
        isZeroCurrency: this.isZeroCurrency,
      });

      const record: TransactionRecord = {
        id: res?.transaction?.id || `tx_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
        agentId: this.activeAgentId,
        targetId,
        targetName,
        targetType: 'avatar',
        amount,
        type: 'tip',
        description,
        timestamp: Date.now(),
        status: 'success',
      };

      await this.recordTransaction(record);
      return record;
    } catch (err: any) {
      const failedRecord: TransactionRecord = {
        id: `tx_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
        agentId: this.activeAgentId,
        targetId,
        targetName,
        targetType: 'avatar',
        amount,
        type: 'tip',
        description: `${description} (Failed: ${err.message || 'Declined'})`,
        timestamp: Date.now(),
        status: 'failed',
      };
      await this.recordTransaction(failedRecord);
      throw new Error(err.message || 'Payment declined or failed');
    }
  }

  /**
   * Background transaction reconciliation: fetch remote history and sync with local cache.
   */
  public async fetchHistory(): Promise<TransactionRecord[]> {
    if (!slBridge.connected) {
      return this.getTransactions();
    }

    try {
      const remote = await slBridge.getTransactionHistory();
      if (typeof remote.balance === 'number' || remote.balance === null) {
        this.balance = remote.balance;
      }
      if (remote.currencySymbol || remote.currency_symbol) {
        this.currencySymbol = remote.currencySymbol || remote.currency_symbol;
      }
      if (typeof remote.isZeroCurrency === 'boolean' || typeof remote.is_zero_currency === 'boolean') {
        this.isZeroCurrency = Boolean(remote.isZeroCurrency ?? remote.is_zero_currency);
      }
      this.emit('balance_updated', {
        balance: this.balance,
        currencySymbol: this.currencySymbol,
        isZeroCurrency: this.isZeroCurrency,
      });

      if (Array.isArray(remote.transactions)) {
        for (const tx of remote.transactions) {
          await this.recordTransaction(tx, false);
        }
      }
    } catch {
      // Offline / fallback to local cache
    }

    return this.getTransactions();
  }

  /**
   * Save transaction to local cache & memory state, enforcing 30-day retention window.
   */
  public async recordTransaction(tx: Partial<TransactionRecord>, emitEvent = true): Promise<void> {
    const record: TransactionRecord = {
      id: tx.id || `tx_${Date.now()}_${Math.random().toString(36).slice(2, 7)}`,
      agentId: tx.agentId || this.activeAgentId,
      targetId: tx.targetId || '',
      targetName: tx.targetName || 'Second Life',
      targetType: tx.targetType || 'avatar',
      amount: Number(tx.amount) || 0,
      type: tx.type || 'payment',
      description: String(tx.description || 'L$ transaction'),
      timestamp: Number(tx.timestamp) || Date.now(),
      status: tx.status || 'success',
    };

    // Prevent duplicate entries
    const existingIndex = this.transactions.findIndex((t) => t.id === record.id);
    if (existingIndex >= 0) {
      this.transactions[existingIndex] = record;
    } else {
      this.transactions.unshift(record);
    }

    // 30-day cutoff filter
    const cutoff = Date.now() - 30 * 24 * 60 * 60 * 1000;
    this.transactions = this.transactions
      .filter((t) => t.timestamp >= cutoff)
      .sort((a, b) => b.timestamp - a.timestamp);

    // Sync to IndexedDB
    await localCache.saveTransaction(this.activeAgentId, record);

    if (emitEvent) {
      this.emit('transaction_added', record);
      this.emit('transactions_updated', this.transactions);
    }
  }

  /**
   * Get 30-day transactions filtered by search query.
   */
  public getTransactions(searchQuery?: string): TransactionRecord[] {
    const cutoff = Date.now() - 30 * 24 * 60 * 60 * 1000;
    const filtered = this.transactions.filter((t) => t.timestamp >= cutoff);

    if (!searchQuery || !searchQuery.trim()) {
      return filtered;
    }

    const q = searchQuery.trim().toLowerCase();
    return filtered.filter(
      (t) =>
        t.description.toLowerCase().includes(q) ||
        t.targetName.toLowerCase().includes(q) ||
        t.targetId.toLowerCase().includes(q) ||
        t.type.toLowerCase().includes(q) ||
        String(t.amount).includes(q)
    );
  }

  /**
   * Calculate summary statistics for 30-day transactions.
   */
  public getStats(): EconomyStats {
    const cutoff = Date.now() - 30 * 24 * 60 * 60 * 1000;
    const valid = this.transactions.filter((t) => t.timestamp >= cutoff && t.status === 'success');

    let totalSpent = 0;
    let totalReceived = 0;
    let oldestTimestamp: number | null = null;

    for (const tx of valid) {
      if (tx.amount < 0 || tx.type === 'payment') {
        totalSpent += Math.abs(tx.amount);
      } else {
        totalReceived += tx.amount;
      }

      if (oldestTimestamp === null || tx.timestamp < oldestTimestamp) {
        oldestTimestamp = tx.timestamp;
      }
    }

    return {
      balance: this.balance,
      currencySymbol: this.currencySymbol,
      isZeroCurrency: this.isZeroCurrency,
      totalTransactions: valid.length,
      totalSpent30Days: totalSpent,
      totalReceived30Days: totalReceived,
      oldestTransactionDate: oldestTimestamp ? new Date(oldestTimestamp).toLocaleDateString() : null,
    };
  }

  /**
   * Clear all local transaction records.
   */
  public async clearHistory(): Promise<void> {
    this.transactions = [];
    await localCache.clearTransactions(this.activeAgentId);
    this.emit('transactions_updated', []);
  }
}

export const economyManager = new EconomyManager();
