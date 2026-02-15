'use client'

import { useState } from 'react'
import WorldView from './WorldView'
import InventoryView from './InventoryView'
import ChatView from './ChatView'
import ProfileView from './ProfileView'
import { getThemeTokens } from '../lib/themes'

interface MainAppProps {
  user: any
  onLogout: () => void
}

type Tab = 'world' | 'inventory' | 'chat' | 'profile'

export default function MainApp({ user, onLogout }: MainAppProps) {
  const [activeTab, setActiveTab] = useState<Tab>('world')
  const theme = getThemeTokens()

  const tabs = [
    { id: 'world' as Tab, name: 'World', icon: '🌍' },
    { id: 'inventory' as Tab, name: 'Inventory', icon: '📦' },
    { id: 'chat' as Tab, name: 'Chat', icon: '💬' },
    { id: 'profile' as Tab, name: 'Profile', icon: '👤' },
  ]

  return (
    <div className="h-screen flex flex-col" style={{ backgroundColor: theme.appBackground, color: theme.textPrimary }}>
      {/* Header */}
      <header className="shadow-sm border-b" style={{ backgroundColor: theme.panelBackground, borderColor: theme.border }}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold" style={{ color: theme.textPrimary }}>Linkpoint</h1>
              <span className="ml-4 text-sm" style={{ color: theme.textSecondary }}>
                {user.fullName} @ {user.grid}
              </span>
            </div>
            <button
              onClick={onLogout}
              className="text-sm font-medium"
              style={{ color: theme.danger }}
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 overflow-hidden">
        {activeTab === 'world' && <WorldView />}
        {activeTab === 'inventory' && <InventoryView />}
        {activeTab === 'chat' && <ChatView user={user} />}
        {activeTab === 'profile' && <ProfileView user={user} onLogout={onLogout} />}
      </main>

      {/* Bottom Navigation */}
      <nav className="border-t mobile-nav" style={{ backgroundColor: theme.panelBackground, borderColor: theme.border }}>
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex justify-around">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className="flex flex-col items-center py-3 px-4 transition-colors"
                style={{ color: activeTab === tab.id ? theme.accent : theme.textSecondary }}
              >
                <span className="text-2xl mb-1">{tab.icon}</span>
                <span className="text-xs font-medium">{tab.name}</span>
              </button>
            ))}
          </div>
        </div>
      </nav>
    </div>
  )
}
