/**
 * Linkpoint - Currency Formatter Module
 *
 * Provides global currency symbol formatting and zero-currency system rendering
 * across all client UI components and views.
 */

export interface CurrencyFormatOptions {
  symbol?: string;
  isZeroCurrency?: boolean;
  showSymbol?: boolean;
  sign?: boolean;
}

/**
 * Format a numeric monetary balance or transaction amount with dynamic currency symbol.
 *
 * @param amount - Number value to format (e.g. 1500)
 * @param symbol - Currency symbol string (e.g. "L$", "OS$", "D$")
 * @param isZeroCurrency - True if grid operates in zero-currency mode
 * @param options - Formatting options
 */
export function formatCurrency(
  amount: number | null | undefined,
  symbol = 'L$',
  isZeroCurrency = false,
  options: CurrencyFormatOptions = {}
): string {
  if (isZeroCurrency) {
    return 'No Currency System';
  }

  if (amount == null || isNaN(Number(amount))) {
    return `0 ${symbol}`.trim();
  }

  const num = Number(amount);
  const activeSymbol = symbol?.trim() || 'L$';
  const showSymbol = options.showSymbol !== false;
  const absFormatted = Math.abs(num).toLocaleString('en-US');

  let prefix = '';
  if (options.sign) {
    prefix = num > 0 ? '+' : num < 0 ? '−' : '';
  } else if (num < 0) {
    prefix = '−';
  }

  if (!showSymbol) {
    return `${prefix}${absFormatted}`;
  }

  return `${prefix}${activeSymbol} ${absFormatted}`.trim();
}

/**
 * Convenience helper to format monetary amounts without zero-currency check override.
 */
export function formatAmount(
  amount: number | null | undefined,
  symbol = 'L$'
): string {
  return formatCurrency(amount, symbol, false);
}
