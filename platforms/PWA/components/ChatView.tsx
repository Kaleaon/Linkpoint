'use client'

import { useState, useRef, useEffect } from 'react'
import {
  ACCESSIBILITY_PRESETS,
  COMFORT_PRESETS,
  DEFAULT_PRESET,
  type AccessibilityPresetId,
  resolveAccessibilityTheme,
} from '../lib/themes'

interface Message {
  id: string
  sender: string
  content: string
  timestamp: Date
  isSent: boolean
}

interface ChatViewProps {
  user: any
}

export default function ChatView({ user }: ChatViewProps) {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      sender: 'System',
      content: 'Welcome to Linkpoint!',
      timestamp: new Date(),
      isSent: false,
    },
    {
      id: '2',
      sender: 'Local Chat',
      content: 'Connected to region',
      timestamp: new Date(),
      isSent: false,
    },
  ])
  const [newMessage, setNewMessage] = useState('')
  const [activeChannel, setActiveChannel] = useState('Local')
  const [activePreset, setActivePreset] = useState<AccessibilityPresetId>(DEFAULT_PRESET)
  const [lastNonComfortPreset, setLastNonComfortPreset] = useState<AccessibilityPresetId>(DEFAULT_PRESET)
  const [reducedGlare, setReducedGlare] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)

  const channels = ['Local', 'Group', 'IM', 'System']
  const isComfortMode = COMFORT_PRESETS.includes(activePreset)
  const theme = resolveAccessibilityTheme(activePreset, reducedGlare)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handlePresetChange = (preset: AccessibilityPresetId) => {
    setActivePreset(preset)
    if (!COMFORT_PRESETS.includes(preset)) {
      setLastNonComfortPreset(preset)
    }
  }

  const handleComfortToggle = () => {
    if (isComfortMode) {
      setActivePreset(lastNonComfortPreset)
      return
    }

    setLastNonComfortPreset(activePreset)
    setActivePreset(theme.isDark ? 'nightRed' : 'sepia')
  }

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault()
    if (!newMessage.trim()) return

    const message: Message = {
      id: Date.now().toString(),
      sender: user.fullName,
      content: newMessage,
      timestamp: new Date(),
      isSent: true,
    }

    setMessages([...messages, message])
    setNewMessage('')

    setTimeout(() => {
      const response: Message = {
        id: (Date.now() + 1).toString(),
        sender: 'Echo Bot',
        content: `You said: "${newMessage}"`,
        timestamp: new Date(),
        isSent: false,
      }
      setMessages(prev => [...prev, response])
    }, 1000)
  }

  return (
    <div className="h-full flex flex-col" style={{ backgroundColor: theme.colors.appBackground, color: theme.colors.textPrimary }}>
      <div className="border-b p-3 space-y-2" style={{ borderColor: theme.colors.border, backgroundColor: theme.colors.panelMutedBackground }}>
        <div className="flex items-center justify-between gap-2">
          <label className="text-xs font-semibold">Preset</label>
          <select
            value={activePreset}
            onChange={(e) => handlePresetChange(e.target.value as AccessibilityPresetId)}
            className="text-sm px-2 py-1 rounded border"
            style={{ backgroundColor: theme.colors.panelBackground, borderColor: theme.colors.border, color: theme.colors.textPrimary }}
          >
            {Object.values(ACCESSIBILITY_PRESETS).map((preset) => (
              <option key={preset.id} value={preset.id}>{preset.name}</option>
            ))}
          </select>
        </div>
        <div className="flex items-center justify-between gap-2">
          <button
            type="button"
            onClick={handleComfortToggle}
            className="px-3 py-1.5 rounded text-sm font-semibold border"
            style={{
              backgroundColor: isComfortMode ? theme.colors.accent : theme.colors.panelBackground,
              color: isComfortMode ? theme.colors.accentText : theme.colors.textPrimary,
              borderColor: theme.colors.border,
            }}
          >
            Comfort Mode {isComfortMode ? 'On' : 'Off'}
          </button>
          <label className="text-xs flex items-center gap-2">
            <input
              type="checkbox"
              checked={reducedGlare}
              onChange={(e) => setReducedGlare(e.target.checked)}
            />
            Reduced Glare
          </label>
        </div>
      </div>

      <div className="border-b" style={{ borderColor: theme.colors.border, backgroundColor: theme.colors.panelMutedBackground }}>
        <div className="flex overflow-x-auto">
          {channels.map((channel) => (
            <button
              key={channel}
              onClick={() => setActiveChannel(channel)}
              className="px-4 py-3 text-sm font-medium whitespace-nowrap border-b-2"
              style={{
                color: activeChannel === channel ? theme.colors.accent : theme.colors.textSecondary,
                borderColor: activeChannel === channel ? theme.colors.accent : 'transparent',
              }}
            >
              {channel}
            </button>
          ))}
        </div>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-4" style={{ backgroundColor: theme.colors.panelBackground }}>
        {messages.map((message) => (
          <div key={message.id} className={`flex ${message.isSent ? 'justify-end' : 'justify-start'}`}>
            <div
              className="max-w-[80%] rounded-lg p-3 mb-2 break-words border"
              style={{
                backgroundColor: message.isSent ? theme.colors.sentBubbleBackground : theme.colors.receivedBubbleBackground,
                color: message.isSent ? theme.colors.sentBubbleText : theme.colors.receivedBubbleText,
                borderColor: theme.colors.border,
              }}
            >
              {!message.isSent && (
                <div className="font-semibold text-sm mb-1">
                  {message.sender}
                </div>
              )}
              <div className="text-sm">{message.content}</div>
              <div
                className="text-xs mt-1"
                style={{ color: message.isSent ? theme.colors.timestampSent : theme.colors.timestampReceived }}
              >
                {message.timestamp.toLocaleTimeString([], {
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </div>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <form onSubmit={handleSendMessage} className="border-t p-4" style={{ borderColor: theme.colors.border, backgroundColor: theme.colors.panelMutedBackground }}>
        <div className="flex space-x-2">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder={`Type a message in ${activeChannel}...`}
            className="flex-1 px-4 py-2 rounded-lg border focus:outline-none"
            style={{ backgroundColor: theme.colors.panelBackground, borderColor: theme.colors.border, color: theme.colors.textPrimary }}
          />
          <button
            type="submit"
            disabled={!newMessage.trim()}
            className="font-semibold py-2 px-4 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ backgroundColor: theme.colors.accent, color: theme.colors.accentText }}
          >
            Send
          </button>
        </div>
      </form>
    </div>
  )
}
