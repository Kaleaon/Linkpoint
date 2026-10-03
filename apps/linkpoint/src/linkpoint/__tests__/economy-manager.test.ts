import { describe, it, expect, beforeEach, vi } from 'vitest';
import { EconomyManager } from '../economy-manager';
import { formatCurrency, formatAmount } from '../currency-formatter';
import { localCache } from '../local-cache';
import { slBridge } from '../sl-bridge';

describe('EconomyManager Subsystem', () => {
  let economy: EconomyManager;

  beforeEach(() => {
    economy = new EconomyManager();
    economy.activeAgentId = 'test-agent-uuid-1234';
    economy.transactions = [];
    economy.balance = 500;
  });

  it('formats currency dynamic symbols and handles zero-currency mode', () => {
    expect(formatCurrency(1500, 'OS$')).toBe('OS$ 1,500');
    expect(formatCurrency(50, 'D$')).toBe('D$ 50');
    expect(formatCurrency(100, 'L$', true)).toBe('No Currency System');
    expect(formatAmount(2500, 'OS$')).toBe('OS$ 2,500');
  });

  it('updates grid currency and suppresses payments on zero-currency grids', async () => {
    economy.setGridCurrency('OS$', false);
    expect(economy.currencySymbol).toBe('OS$');
    expect(economy.isZeroCurrency).toBe(false);

    economy.setGridCurrency('', true);
    expect(economy.isZeroCurrency).toBe(true);

    await expect(
      economy.payObject({ targetId: 'obj-123', amount: 50 })
    ).rejects.toThrow('Payments are disabled on zero-currency grids');

    await expect(
      economy.payAvatar({ targetId: 'avatar-123', amount: 50 })
    ).rejects.toThrow('Payments are disabled on zero-currency grids');
  });

  it('provides standard quick-tip presets', () => {
    const presets = economy.getQuickTipPresets();
    expect(presets).toEqual([5, 10, 50, 100]);
  });

  it('records transaction and maintains 30-day retention window', async () => {
    const now = Date.now();
    const dayMs = 24 * 60 * 60 * 1000;

    // Recent transaction (within 30 days)
    const recentTx = {
      id: 'tx-recent-1',
      agentId: 'test-agent-uuid-1234',
      targetId: 'avatar-uuid-1',
      targetName: 'Aimee Resident',
      targetType: 'avatar' as const,
      amount: 50,
      type: 'tip' as const,
      description: 'Tip for DJ set',
      timestamp: now - 5 * dayMs,
      status: 'success' as const,
    };

    // Old transaction (> 30 days old)
    const oldTx = {
      id: 'tx-old-1',
      agentId: 'test-agent-uuid-1234',
      targetId: 'avatar-uuid-2',
      targetName: 'Bob Resident',
      targetType: 'avatar' as const,
      amount: 100,
      type: 'tip' as const,
      description: 'Old tip',
      timestamp: now - 35 * dayMs,
      status: 'success' as const,
    };

    await economy.recordTransaction(recentTx);
    await economy.recordTransaction(oldTx);

    const history = economy.getTransactions();
    expect(history.length).toBe(1);
    expect(history[0].id).toBe('tx-recent-1');
  });

  it('filters transactions by search query', async () => {
    const now = Date.now();

    await economy.recordTransaction({
      id: 'tx-1',
      description: 'Club Tip Jar',
      targetName: 'Tipping Object',
      targetId: 'obj-555',
      amount: 25,
      type: 'payment',
      timestamp: now - 1000,
      status: 'success',
    });

    await economy.recordTransaction({
      id: 'tx-2',
      description: 'Gift for friend',
      targetName: 'Samantha',
      targetId: 'avatar-200',
      amount: 100,
      type: 'tip',
      timestamp: now - 500,
      status: 'success',
    });

    const matchDescription = economy.getTransactions('Club');
    expect(matchDescription.length).toBe(1);
    expect(matchDescription[0].id).toBe('tx-1');

    const matchName = economy.getTransactions('Samantha');
    expect(matchName.length).toBe(1);
    expect(matchName[0].id).toBe('tx-2');

    const matchAmount = economy.getTransactions('100');
    expect(matchAmount.length).toBe(1);
    expect(matchAmount[0].id).toBe('tx-2');

    const noMatch = economy.getTransactions('NonExistentKeyword');
    expect(noMatch.length).toBe(0);
  });

  it('calculates economy statistics accurately', async () => {
    const now = Date.now();

    await economy.recordTransaction({
      id: 'tx-spend-1',
      amount: -50,
      type: 'payment',
      description: 'Object purchase',
      timestamp: now - 2000,
      status: 'success',
    });

    await economy.recordTransaction({
      id: 'tx-receive-1',
      amount: 200,
      type: 'transfer',
      description: 'Payment received',
      timestamp: now - 1000,
      status: 'success',
    });

    const stats = economy.getStats();
    expect(stats.balance).toBe(500);
    expect(stats.totalTransactions).toBe(2);
    expect(stats.totalSpent30Days).toBe(50);
    expect(stats.totalReceived30Days).toBe(200);
  });

  it('handles payObject RPC execution and errors', async () => {
    vi.spyOn(slBridge, 'payObject').mockResolvedValueOnce({
      paid: true,
      targetId: 'obj-uuid-999',
      amount: 50,
      description: 'Test Object Payment',
    });

    const tx = await economy.payObject({
      targetId: 'obj-uuid-999',
      targetName: 'Tip Vendor',
      amount: 50,
      description: 'Test Object Payment',
    });

    expect(tx.targetId).toBe('obj-uuid-999');
    expect(tx.amount).toBe(50);
    expect(tx.status).toBe('success');
  });

  it('rejects invalid payment requests with zero or negative amounts', async () => {
    await expect(
      economy.payObject({
        targetId: 'obj-uuid-1',
        amount: 0,
      })
    ).rejects.toThrow('Payment amount must be a positive integer');

    await expect(
      economy.payAvatar({
        targetId: 'avatar-uuid-1',
        amount: -10,
      })
    ).rejects.toThrow('Payment amount must be a positive integer');
  });

  it('handles RPC errors for declined payments', async () => {
    vi.spyOn(slBridge, 'payAvatar').mockRejectedValueOnce(new Error('Insufficient funds to complete payment'));

    await expect(
      economy.payAvatar({
        targetId: 'avatar-uuid-fail',
        targetName: 'Declined Resident',
        amount: 1000,
      })
    ).rejects.toThrow('Insufficient funds to complete payment');

    // Verify failed transaction was logged in history
    const failedTx = economy.transactions.find((t) => t.targetId === 'avatar-uuid-fail');
    expect(failedTx).toBeDefined();
    expect(failedTx?.status).toBe('failed');
  });
});
