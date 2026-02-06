'use client'

import { useState } from 'react'
import WorldView from './WorldView'
import InventoryView from './InventoryView'
import ChatView from './ChatView'
import ProfileView from './ProfileView'

interface MainAppProps {
  user: any
  onLogout: () => void
}

type Tab = 'world' | 'inventory' | 'chat' | 'profile'

export default function MainApp({ user, onLogout }: MainAppProps) {
  const [activeTab, setActiveTab] = useState<Tab>('world')

  const tabs = [
    { id: 'world' as Tab, name: 'World', icon: '🌍' },
    { id: 'inventory' as Tab, name: 'Inventory', icon: '📦' },
    { id: 'chat' as Tab, name: 'Chat', icon: '💬' },
    { id: 'profile' as Tab, name: 'Profile', icon: '👤' },
  ]

  return (
    <div className="h-screen flex flex-col bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold text-gray-900">Linkpoint</h1>
              <span className="ml-4 text-sm text-gray-600">
                {user.fullName} @ {user.grid}
              </span>
            </div>
            <button
              onClick={onLogout}
              className="text-sm text-red-600 hover:text-red-700 font-medium"
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
      <nav className="bg-white border-t border-gray-200 mobile-nav">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex justify-around">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex flex-col items-center py-3 px-4 transition-colors ${
                  activeTab === tab.id
                    ? 'text-blue-600'
                    : 'text-gray-600 hover:text-gray-900'
                }`}
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