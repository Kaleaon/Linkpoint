import React, { useState, useEffect, useRef, useMemo } from 'react';
import {
  GridConsole,
  LogEntry,
  LogLevel,
  LOG_LEVELS,
  formatLogTime,
  formatLogEntry
} from './GridConsole';

interface GridConsolePanelProps {
  console: GridConsole;
  /** Rows of log output to show before scrolling. */
  height?: number;
}

const LEVEL_COLORS: Record<LogLevel, string> = {
  debug: '#8a8a8a',
  info: '#4ec9b0',
  warn: '#d7ba7d',
  error: '#f48771',
  fatal: '#ff5370'
};

export const GridConsolePanel: React.FC<GridConsolePanelProps> = ({ console: gridConsole, height = 260 }) => {
  const [entries, setEntries] = useState<LogEntry[]>(() => gridConsole.getEntries());
  const [levelFilter, setLevelFilter] = useState<LogLevel>('debug');
  const [search, setSearch] = useState('');
  const [autoScroll, setAutoScroll] = useState(true);
  const scrollRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const refresh = () => setEntries(gridConsole.getEntries());
    gridConsole.on('logEntry', refresh);
    gridConsole.on('logCleared', refresh);
    refresh();
    return () => {
      gridConsole.off('logEntry', refresh);
      gridConsole.off('logCleared', refresh);
    };
  }, [gridConsole]);

  const visible = useMemo(
    () => gridConsole.getEntries({ level: levelFilter, search: search || undefined }),
    // `entries` drives recomputation as the log changes.
    [gridConsole, levelFilter, search, entries]
  );

  useEffect(() => {
    if (autoScroll && scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [visible.length, autoScroll]);

  const counts = gridConsole.getCountsByLevel();
  const problemCount = counts.error + counts.fatal;

  const handleCopy = () => {
    const text = gridConsole.toText({ level: levelFilter, search: search || undefined });
    if (typeof navigator !== 'undefined' && navigator.clipboard?.writeText) {
      navigator.clipboard.writeText(text).catch(err =>
        gridConsole.captureError('CONSOLE', 'Could not copy the log to the clipboard.', err)
      );
    }
  };

  const handleDownload = () => {
    const text = gridConsole.toText({ level: levelFilter, search: search || undefined });
    try {
      const blob = new Blob([text], { type: 'text/plain' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `linkpoint-offline-grid-${new Date().toISOString().replace(/[:.]/g, '-')}.log`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    } catch (err) {
      gridConsole.captureError('CONSOLE', 'Could not download the log file.', err);
    }
  };

  return (
    <div style={{ background: '#f8f9fa', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
      <h3 style={{ marginTop: 0 }}>
        Grid Console &amp; Error Log{' '}
        <span style={{ fontSize: '13px', fontWeight: 'normal', color: problemCount > 0 ? '#c9302c' : '#5cb85c' }}>
          ({gridConsole.getEntryCount()} entries, {counts.warn} warning{counts.warn === 1 ? '' : 's'}, {problemCount} error
          {problemCount === 1 ? '' : 's'})
        </span>
      </h3>

      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', alignItems: 'center', marginBottom: '10px' }}>
        <label htmlFor="console-level">Level: </label>
        <select
          id="console-level"
          value={levelFilter}
          onChange={e => setLevelFilter(e.target.value as LogLevel)}
          style={{ padding: '4px' }}
        >
          {LOG_LEVELS.map(level => (
            <option key={level} value={level}>
              {level.toUpperCase()} and above
            </option>
          ))}
        </select>

        <input
          type="search"
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Filter log..."
          aria-label="Filter log"
          style={{ padding: '4px', flex: '1 1 160px', minWidth: '120px' }}
        />

        <label style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
          <input type="checkbox" checked={autoScroll} onChange={e => setAutoScroll(e.target.checked)} />
          Auto-scroll
        </label>

        <button type="button" onClick={handleCopy} style={{ padding: '6px 12px' }}>Copy</button>
        <button type="button" onClick={handleDownload} style={{ padding: '6px 12px' }}>Download</button>
        <button type="button" onClick={() => gridConsole.clear()} style={{ padding: '6px 12px' }}>Clear</button>
      </div>

      <div
        ref={scrollRef}
        role="log"
        aria-label="Local grid console output"
        style={{
          height: `${height}px`,
          overflowY: 'auto',
          background: '#1e1e1e',
          color: '#d4d4d4',
          fontFamily: 'Menlo, Consolas, monospace',
          fontSize: '12px',
          lineHeight: 1.5,
          padding: '10px',
          borderRadius: '4px',
          whiteSpace: 'pre-wrap',
          wordBreak: 'break-word'
        }}
      >
        {visible.length === 0 ? (
          <div style={{ color: '#8a8a8a' }}>No console output yet.</div>
        ) : (
          visible.map(entry => (
            <div key={entry.id} title={formatLogEntry(entry, true)}>
              <span style={{ color: '#8a8a8a' }}>{formatLogTime(entry.timestamp)} </span>
              <span style={{ color: LEVEL_COLORS[entry.level], fontWeight: 'bold' }}>
                {entry.level.toUpperCase().padEnd(5, '\u00a0')}
              </span>
              <span style={{ color: '#569cd6' }}> [{entry.component}]: </span>
              <span>{entry.message}</span>
              {entry.detail && (
                <div style={{ color: '#8a8a8a', paddingLeft: '16px' }}>{entry.detail}</div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};
