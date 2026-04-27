'use client'

import { useState, useRef, useEffect } from 'react'
import { getThemeTokens } from '../lib/themes'

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
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const theme = getThemeTokens()

  const channels = ['Local', 'Group', 'IM', 'System']

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault()
    if (!newMessage.trim()) return

    const pendingMessage = newMessage
    const message: Message = {
      id: Date.now().toString(),
      sender: user.fullName,
      content: pendingMessage,
      timestamp: new Date(),
      isSent: true,
    }

    setMessages([...messages, message])
    setNewMessage('')

    setTimeout(() => {
      const response: Message = {
        id: (Date.now() + 1).toString(),
        sender: 'Echo Bot',
        content: `You said: "${pendingMessage}"`,
        timestamp: new Date(),
        isSent: false,
      }
      setMessages(prev => [...prev, response])
    }, 1000)
  }

  return (
    <div className="h-full flex flex-col" style={{ backgroundColor: theme.panelBackground }}>
      {/* Channel Tabs */}
      <div className="border-b" style={{ borderColor: theme.border, backgroundColor: theme.panelMuted }}>
        <div className="flex overflow-x-auto">
          {channels.map((channel) => (
            <button
              key={channel}
              onClick={() => setActiveChannel(channel)}
              className="px-4 py-3 text-sm font-medium whitespace-nowrap transition-colors border-b-2"
              style={{
                color: activeChannel === channel ? theme.accent : theme.textSecondary,
                borderColor: activeChannel === channel ? theme.accent : 'transparent',
              }}
            >
              {channel}
            </button>
          ))}
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((message) => (
          <div key={message.id} className={`flex ${message.isSent ? 'justify-end' : 'justify-start'}`}>
            <div
              className="rounded-lg p-3 mb-2 max-w-[80%] break-words"
              style={{
                backgroundColor: message.isSent ? theme.sentBubble : theme.receivedBubble,
                color: message.isSent ? theme.accentContrast : theme.textPrimary,
                border: `1px solid ${theme.border}`,
              }}
            >
              {!message.isSent && (
                <div className="font-semibold text-sm mb-1" style={{ color: theme.accent }}>
                  {message.sender}
                </div>
              )}
              <div className="text-sm">{message.content}</div>
              <div className="text-xs mt-1" style={{ color: message.isSent ? theme.accentContrast : theme.textMuted }}>
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

      {/* Input */}
      <form onSubmit={handleSendMessage} className="border-t p-4" style={{ borderColor: theme.border }}>
        <div className="flex space-x-2">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder={`Type a message in ${activeChannel}...`}
            className="flex-1 w-full px-4 py-2 rounded-lg focus:outline-none"
            style={{
              backgroundColor: theme.inputBackground,
              border: `1px solid ${theme.inputBorder}`,
              color: theme.textPrimary,
            }}
          />
          <button
            type="submit"
            disabled={!newMessage.trim()}
            className="font-semibold py-2 px-4 rounded-lg transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
            style={{ backgroundColor: theme.accentStrong, color: theme.accentContrast }}
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </button>
        </div>
      </form>
    </div>
  )
}
