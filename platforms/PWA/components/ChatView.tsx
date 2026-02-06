'use client'

import { useState, useRef, useEffect } from 'react'

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

    const message: Message = {
      id: Date.now().toString(),
      sender: user.fullName,
      content: newMessage,
      timestamp: new Date(),
      isSent: true,
    }

    setMessages([...messages, message])
    setNewMessage('')

    // Simulate a response
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
    <div className="h-full flex flex-col bg-white">
      {/* Channel Tabs */}
      <div className="border-b border-gray-200 bg-gray-50">
        <div className="flex overflow-x-auto">
          {channels.map((channel) => (
            <button
              key={channel}
              onClick={() => setActiveChannel(channel)}
              className={`px-4 py-3 text-sm font-medium whitespace-nowrap transition-colors ${
                activeChannel === channel
                  ? 'text-blue-600 border-b-2 border-blue-600'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              {channel}
            </button>
          ))}
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`flex ${message.isSent ? 'justify-end' : 'justify-start'}`}
          >
            <div
              className={`max-w-[80%] ${
                message.isSent
                  ? 'chat-bubble chat-bubble-sent'
                  : 'chat-bubble chat-bubble-received'
              }`}
            >
              {!message.isSent && (
                <div className="font-semibold text-sm mb-1">
                  {message.sender}
                </div>
              )}
              <div className="text-sm">{message.content}</div>
              <div
                className={`text-xs mt-1 ${
                  message.isSent ? 'text-blue-100' : 'text-gray-500'
                }`}
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

      {/* Input */}
      <form onSubmit={handleSendMessage} className="border-t border-gray-200 p-4">
        <div className="flex space-x-2">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder={`Type a message in ${activeChannel}...`}
            className="flex-1 input-field"
          />
          <button
            type="submit"
            disabled={!newMessage.trim()}
            className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"
              />
            </svg>
          </button>
        </div>
      </form>
    </div>
  )
}