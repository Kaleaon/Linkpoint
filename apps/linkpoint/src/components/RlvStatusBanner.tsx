import React from 'react';
import { useRlvSafe } from '../viewer/RlvContext';

export const RlvStatusBanner: React.FC<{ className?: string }> = ({ className }) => {
  const {
    enabled,
    objectRestrictions,
    objectNames,
    pendingPrompts,
    toasts,
    approvePrompt,
    denyPrompt,
    alwaysAllowPrompt,
    clearObjectRestrictions,
    clearAllRestrictions,
    dismissToast,
  } = useRlvSafe();

  if (!enabled) return null;

  const hasPendingPrompts = pendingPrompts.length > 0;
  const hasActiveRestrictions = objectRestrictions.size > 0;
  const hasToasts = toasts.length > 0;

  if (!hasPendingPrompts && !hasActiveRestrictions && !hasToasts) return null;

  return (
    <div className={`rlv-banner-container ${className || ''}`} style={{ margin: '8px', fontFamily: 'sans-serif' }}>
      {/* Real-Time Soft Restriction Toasts */}
      {hasToasts && (
        <div className="rlv-toasts-area" style={{ marginBottom: '8px' }}>
          {toasts.map((toast) => (
            <div
              key={toast.id}
              className="rlv-toast-item"
              style={{
                background: '#1e293b',
                color: '#f8fafc',
                borderLeft: '4px solid #3b82f6',
                padding: '8px 12px',
                borderRadius: '4px',
                marginBottom: '4px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                fontSize: '12px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
              }}
            >
              <div>
                <strong>[RLV Notification]</strong> {toast.objectName} ({toast.objectUuid}): Restriction{' '}
                <code style={{ background: '#334155', padding: '2px 4px', borderRadius: '2px' }}>{toast.restriction}</code>{' '}
                was {toast.action}.
              </div>
              <button
                onClick={() => dismissToast(toast.id)}
                style={{
                  background: 'transparent',
                  border: 'none',
                  color: '#94a3b8',
                  cursor: 'pointer',
                  fontSize: '14px',
                  marginLeft: '8px',
                }}
                aria-label="Dismiss toast"
              >
                ✕
              </button>
            </div>
          ))}
        </div>
      )}

      {/* High-Impact Interactive Confirmation Prompts for Tier 2 Forced Actions */}
      {hasPendingPrompts && (
        <div className="rlv-prompts-area" style={{ marginBottom: '8px' }}>
          {pendingPrompts.map((prompt) => (
            <div
              key={prompt.promptId}
              className="rlv-prompt-card"
              style={{
                background: '#7f1d1d',
                color: '#fef2f2',
                border: '2px solid #ef4444',
                padding: '12px 16px',
                borderRadius: '6px',
                marginBottom: '8px',
                boxShadow: '0 4px 6px rgba(0,0,0,0.3)',
              }}
            >
              <div style={{ fontWeight: 'bold', fontSize: '14px', marginBottom: '4px' }}>
                ⚠️ RLV Forced Action Request
              </div>
              <div style={{ fontSize: '12px', marginBottom: '8px' }}>
                Object <strong>{prompt.objectName}</strong> (UUID: <code style={{ fontSize: '11px' }}>{prompt.objectUuid}</code>) is requesting forced action:{' '}
                <strong style={{ textDecoration: 'underline' }}>{prompt.forcedAction}</strong>.
              </div>
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                <button
                  onClick={() => approvePrompt(prompt.promptId)}
                  style={{
                    background: '#22c55e',
                    color: '#ffffff',
                    border: 'none',
                    padding: '6px 12px',
                    borderRadius: '4px',
                    fontWeight: 'bold',
                    cursor: 'pointer',
                  }}
                >
                  Approve
                </button>
                <button
                  onClick={() => denyPrompt(prompt.promptId)}
                  style={{
                    background: '#dc2626',
                    color: '#ffffff',
                    border: 'none',
                    padding: '6px 12px',
                    borderRadius: '4px',
                    fontWeight: 'bold',
                    cursor: 'pointer',
                  }}
                >
                  Deny
                </button>
                <button
                  onClick={() => alwaysAllowPrompt(prompt.promptId)}
                  style={{
                    background: '#2563eb',
                    color: '#ffffff',
                    border: 'none',
                    padding: '6px 12px',
                    borderRadius: '4px',
                    fontWeight: 'bold',
                    cursor: 'pointer',
                  }}
                >
                  Always allow for this session
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Active Restrictions Status Banner */}
      {hasActiveRestrictions && (
        <div
          className="rlv-status-banner"
          style={{
            background: '#0f172a',
            color: '#e2e8f0',
            border: '1px solid #334155',
            padding: '12px',
            borderRadius: '6px',
            fontSize: '12px',
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
            <span style={{ fontWeight: 'bold', color: '#38bdf8' }}>🔒 Active RLV Restrictions</span>
            <button
              onClick={() => clearAllRestrictions()}
              style={{
                background: '#ef4444',
                color: '#white',
                border: 'none',
                padding: '4px 8px',
                borderRadius: '4px',
                fontSize: '11px',
                cursor: 'pointer',
              }}
            >
              Clear All Restrictions
            </button>
          </div>

          <div className="rlv-object-list" style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {Array.from(objectRestrictions.entries()).map(([uuid, restrictions]) => {
              const name = objectNames.get(uuid) || uuid;
              const restrictionList = Array.from(restrictions);
              return (
                <div
                  key={uuid}
                  style={{
                    background: '#1e293b',
                    padding: '8px',
                    borderRadius: '4px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 'bold', fontSize: '12px', color: '#f1f5f9' }}>
                      {name} <span style={{ fontSize: '10px', color: '#94a3b8' }}>({uuid})</span>
                    </div>
                    <div style={{ fontSize: '11px', color: '#cbd5e1', marginTop: '2px' }}>
                      Restrictions: {restrictionList.join(', ')}
                    </div>
                  </div>
                  <button
                    onClick={() => clearObjectRestrictions(uuid)}
                    style={{
                      background: '#475569',
                      color: '#f8fafc',
                      border: 'none',
                      padding: '4px 8px',
                      borderRadius: '4px',
                      fontSize: '11px',
                      cursor: 'pointer',
                    }}
                  >
                    Clear
                  </button>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};
