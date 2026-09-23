/**
 * Linkpoint Offline Grid - Console & Error Log
 *
 * An in-viewer equivalent of the OpenSim region console: a bounded, level-aware
 * log that every offline service writes to, and that the UI can render, filter
 * and export. Entries are kept in a ring buffer so a long-running local grid
 * cannot grow the log without bound.
 */

import { Utils } from '../utils';

export type LogLevel = 'debug' | 'info' | 'warn' | 'error' | 'fatal';

/** Severity ordering, used for threshold filtering. */
export const LOG_LEVEL_ORDER: Record<LogLevel, number> = {
  debug: 0,
  info: 1,
  warn: 2,
  error: 3,
  fatal: 4
};

export const LOG_LEVELS: LogLevel[] = ['debug', 'info', 'warn', 'error', 'fatal'];

/** OpenSim-style component tags, used as the `[TAG]` in each console line. */
export const LOG_COMPONENTS = {
  GRID: 'LOCAL GRID',
  LOGIN: 'LOGIN SERVICE',
  USER: 'USER SERVICE',
  REGION: 'REGION',
  ASSET: 'ASSET SERVICE',
  INVENTORY: 'INVENTORY SERVICE',
  ARCHIVER: 'ARCHIVER',
  CACHE: 'CACHE',
  HYPERGRID: 'HYPERGRID',
  CONSOLE: 'CONSOLE'
} as const;

export interface LogEntry {
  id: string;
  timestamp: number;
  level: LogLevel;
  component: string;
  message: string;
  /** Optional extra context (stack trace, payload summary). */
  detail?: string;
}

export interface LogFilter {
  /** Minimum severity to include. */
  level?: LogLevel;
  /** Exact component tag to include. */
  component?: string;
  /** Case-insensitive substring match over message, detail and component. */
  search?: string;
}

export const DEFAULT_MAX_ENTRIES = 500;
export const MAX_ENTRIES_LIMIT = 10000;

function pad(value: number, size: number = 2): string {
  return String(value).padStart(size, '0');
}

/** `16:30:33,123` - the time portion of an OpenSim log line. */
export function formatLogTime(timestamp: number): string {
  const date = new Date(timestamp);
  return `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())},${pad(date.getMilliseconds(), 3)}`;
}

function formatLogDate(timestamp: number): string {
  const date = new Date(timestamp);
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

/** `16:30:33,123 INFO  [REGION]: message` */
export function formatLogEntry(entry: LogEntry, includeDate: boolean = false): string {
  const stamp = includeDate
    ? `${formatLogDate(entry.timestamp)} ${formatLogTime(entry.timestamp)}`
    : formatLogTime(entry.timestamp);
  const level = entry.level.toUpperCase().padEnd(5, ' ');
  const detail = entry.detail ? `\n    ${entry.detail.replace(/\n/g, '\n    ')}` : '';
  return `${stamp} ${level} [${entry.component}]: ${entry.message}${detail}`;
}

export class GridConsole extends Utils.EventEmitter {
  private entries: LogEntry[] = [];
  private maxEntries: number;
  private minLevel: LogLevel = 'debug';
  private mirrorToBrowserConsole: boolean = false;
  private detachGlobalHandlers: (() => void) | null = null;

  constructor(maxEntries: number = DEFAULT_MAX_ENTRIES) {
    super();
    this.maxEntries = Utils.clamp(maxEntries, 1, MAX_ENTRIES_LIMIT);
  }

  /** Record an entry. Returns null when the level is below the threshold. */
  public log(level: LogLevel, component: string, message: string, detail?: string): LogEntry | null {
    if (LOG_LEVEL_ORDER[level] < LOG_LEVEL_ORDER[this.minLevel]) {
      return null;
    }

    const entry: LogEntry = {
      id: Utils.generateUUID(),
      timestamp: Date.now(),
      level,
      component,
      message,
      ...(detail ? { detail } : {})
    };

    this.entries.push(entry);
    const overflow = this.entries.length - this.maxEntries;
    if (overflow > 0) {
      this.entries.splice(0, overflow);
    }

    if (this.mirrorToBrowserConsole) {
      this.mirror(entry);
    }

    this.emit('logEntry', entry);
    return entry;
  }

  private mirror(entry: LogEntry) {
    const line = formatLogEntry(entry);
    if (entry.level === 'error' || entry.level === 'fatal') console.error(line);
    else if (entry.level === 'warn') console.warn(line);
    else if (entry.level === 'debug') console.debug(line);
    else console.info(line);
  }

  public debug(component: string, message: string, detail?: string) {
    return this.log('debug', component, message, detail);
  }

  public info(component: string, message: string, detail?: string) {
    return this.log('info', component, message, detail);
  }

  public warn(component: string, message: string, detail?: string) {
    return this.log('warn', component, message, detail);
  }

  public error(component: string, message: string, detail?: string) {
    return this.log('error', component, message, detail);
  }

  public fatal(component: string, message: string, detail?: string) {
    return this.log('fatal', component, message, detail);
  }

  /** Log a thrown value, preserving its message and stack as entry detail. */
  public captureError(component: string, message: string, error: unknown): LogEntry | null {
    let detail: string;
    if (error instanceof Error) {
      detail = error.stack || `${error.name}: ${error.message}`;
    } else if (typeof error === 'string') {
      detail = error;
    } else {
      try {
        detail = JSON.stringify(error);
      } catch (e) {
        detail = String(error);
      }
    }
    return this.error(component, message, detail);
  }

  public getEntries(filter?: LogFilter): LogEntry[] {
    let result = this.entries.slice();

    if (filter?.level) {
      const threshold = LOG_LEVEL_ORDER[filter.level];
      result = result.filter(e => LOG_LEVEL_ORDER[e.level] >= threshold);
    }
    if (filter?.component) {
      result = result.filter(e => e.component === filter.component);
    }
    if (filter?.search) {
      const needle = filter.search.toLowerCase();
      result = result.filter(e =>
        e.message.toLowerCase().includes(needle) ||
        e.component.toLowerCase().includes(needle) ||
        (e.detail ? e.detail.toLowerCase().includes(needle) : false)
      );
    }

    return result;
  }

  public getEntryCount(): number {
    return this.entries.length;
  }

  /** Per-level totals, for the console's status badges. */
  public getCountsByLevel(): Record<LogLevel, number> {
    const counts: Record<LogLevel, number> = { debug: 0, info: 0, warn: 0, error: 0, fatal: 0 };
    for (const entry of this.entries) {
      counts[entry.level]++;
    }
    return counts;
  }

  public getComponents(): string[] {
    return Array.from(new Set(this.entries.map(e => e.component))).sort();
  }

  public clear() {
    this.entries = [];
    this.emit('logCleared', undefined);
  }

  public setMaxEntries(maxEntries: number) {
    this.maxEntries = Utils.clamp(Math.floor(maxEntries), 1, MAX_ENTRIES_LIMIT);
    const overflow = this.entries.length - this.maxEntries;
    if (overflow > 0) {
      this.entries.splice(0, overflow);
    }
    return this.maxEntries;
  }

  public getMaxEntries(): number {
    return this.maxEntries;
  }

  public setMinLevel(level: LogLevel) {
    this.minLevel = level;
  }

  public getMinLevel(): LogLevel {
    return this.minLevel;
  }

  public setMirrorToBrowserConsole(enabled: boolean) {
    this.mirrorToBrowserConsole = enabled;
  }

  /** Export the (optionally filtered) log as plain text for copy or download. */
  public toText(filter?: LogFilter): string {
    return this.getEntries(filter)
      .map(entry => formatLogEntry(entry, true))
      .join('\n');
  }

  /**
   * Route uncaught errors and rejected promises into the console so viewer-level
   * failures show up in the same log as grid events. Returns a detach function;
   * calling this twice detaches the previous handlers first.
   */
  public attachGlobalErrorHandlers(): () => void {
    if (this.detachGlobalHandlers) {
      this.detachGlobalHandlers();
    }
    if (typeof window === 'undefined' || typeof window.addEventListener !== 'function') {
      this.detachGlobalHandlers = null;
      return () => {};
    }

    const onError = (event: ErrorEvent) => {
      this.captureError(
        LOG_COMPONENTS.CONSOLE,
        `Uncaught error: ${event.message || 'unknown error'}`,
        event.error ?? event.message
      );
    };
    const onRejection = (event: PromiseRejectionEvent) => {
      this.captureError(LOG_COMPONENTS.CONSOLE, 'Unhandled promise rejection', event.reason);
    };

    window.addEventListener('error', onError);
    window.addEventListener('unhandledrejection', onRejection);

    const detach = () => {
      window.removeEventListener('error', onError);
      window.removeEventListener('unhandledrejection', onRejection);
      this.detachGlobalHandlers = null;
    };
    this.detachGlobalHandlers = detach;
    return detach;
  }
}

/**
 * Shared console instance. The offline services are constructed independently
 * but log to one stream, so the UI can render a single unified grid console.
 */
export const gridConsole = new GridConsole();
