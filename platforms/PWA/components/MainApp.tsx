'use client'

import { useMemo, useState } from 'react'
import WorldView from './WorldView'
import InventoryView from './InventoryView'
import ChatView from './ChatView'
import ProfileView from './ProfileView'
import ThemeSelector, { getThemePresetById } from './ThemeSelector'

interface MainAppProps {
  user: any
  onLogout: () => void
}

type Tab = 'world' | 'inventory' | 'chat' | 'profile'
type TabThemeOverrides = Partial<Record<Tab, string>>

const defaultTabThemes: Record<Tab, string> = {
  world: 'midnight',
  chat: 'nord',
  inventory: 'paper',
  profile: 'classic-light',
}

export default function MainApp({ user, onLogout }: MainAppProps) {
  const [activeTab, setActiveTab] = useState<Tab>('world')
  const [showThemePanel, setShowThemePanel] = useState(false)
  const [globalThemeId, setGlobalThemeId] = useState('gold')
  const [tabThemeOverrides, setTabThemeOverrides] = useState<TabThemeOverrides>(defaultTabThemes)

  const tabs = [
    { id: 'world' as Tab, name: 'World', icon: '🌍' },
    { id: 'inventory' as Tab, name: 'Inventory', icon: '📦' },
    { id: 'chat' as Tab, name: 'Chat', icon: '💬' },
    { id: 'profile' as Tab, name: 'Profile', icon: '👤' },
  ]

  const activeThemeId = tabThemeOverrides[activeTab] || globalThemeId
  const activeThemeName = getThemePresetById(activeThemeId)?.name || activeThemeId

  const currentThemeStyles = useMemo(() => {
    const category = getThemePresetById(activeThemeId)?.category

    switch (category) {
      case 'Dark':
        return {
          app: 'bg-slate-900',
          header: 'bg-slate-800 border-slate-700 text-white',
          nav: 'bg-slate-800 border-slate-700',
          subtitle: 'text-slate-300',
          inactiveTab: 'text-slate-400 hover:text-white',
        }
      case 'Light/Reading':
        return {
          app: 'bg-amber-50',
          header: 'bg-white border-amber-200 text-gray-900',
          nav: 'bg-white border-amber-200',
          subtitle: 'text-gray-600',
          inactiveTab: 'text-gray-600 hover:text-gray-900',
        }
      case 'Accessibility':
        return {
          app: 'bg-white',
          header: 'bg-black border-black text-white',
          nav: 'bg-black border-black',
          subtitle: 'text-yellow-300',
          inactiveTab: 'text-gray-300 hover:text-white',
        }
      case 'Metallic':
        return {
          app: 'bg-slate-100',
          header: 'bg-slate-200 border-slate-300 text-slate-900',
          nav: 'bg-slate-200 border-slate-300',
          subtitle: 'text-slate-700',
          inactiveTab: 'text-slate-600 hover:text-slate-900',
        }
      case 'Accent':
      default:
        return {
          app: 'bg-rose-50',
          header: 'bg-amber-100 border-amber-300 text-gray-900',
          nav: 'bg-amber-100 border-amber-300',
          subtitle: 'text-amber-900',
          inactiveTab: 'text-amber-800 hover:text-amber-950',
        }
    }
  }, [activeThemeId])

  return (
    <div className={`h-screen flex flex-col ${currentThemeStyles.app}`}>
      {/* Header */}
      <header className={`shadow-sm border-b ${currentThemeStyles.header}`}>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold">Linkpoint</h1>
              <span className={`ml-4 text-sm ${currentThemeStyles.subtitle}`}>
                {user.fullName} @ {user.grid}
              </span>
            </div>
            <div className="flex items-center gap-4">
              <button
                type="button"
                onClick={() => setShowThemePanel((show) => !show)}
                className="text-sm font-medium px-3 py-2 rounded-lg border border-current/20 hover:bg-black/5"
              >
                Theme: {activeThemeName}
              </button>
              <button
                onClick={onLogout}
                className="text-sm text-red-600 hover:text-red-700 font-medium"
              >
                Logout
              </button>
            </div>
          </div>
          {showThemePanel && (
            <div className="pb-4">
              <ThemeSelector
                label={`Theme for ${activeTab}`}
                selectedThemeId={activeThemeId}
                onSelectTheme={() => {}}
                onUseForAllTabs={(themeId) => {
                  setGlobalThemeId(themeId)
                  setTabThemeOverrides({})
                }}
                onUseForThisTabOnly={(themeId) => {
                  setTabThemeOverrides((current) => ({
                    ...current,
                    [activeTab]: themeId,
                  }))
                }}
              />
            </div>
          )}
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 overflow-hidden">
        {activeTab === 'world' && <WorldView />}
        {activeTab === 'inventory' && <InventoryView />}
        {activeTab === 'chat' && <ChatView user={user} />}
        {activeTab === 'profile' && (
          <ProfileView
            user={user}
            onLogout={onLogout}
            globalThemeId={globalThemeId}
            onGlobalThemeChange={setGlobalThemeId}
            tabThemeOverrides={tabThemeOverrides}
            onTabThemeChange={(tab, themeId) => {
              setTabThemeOverrides((current) => ({
                ...current,
                [tab]: themeId,
              }))
            }}
            onClearTabTheme={(tab) => {
              setTabThemeOverrides((current) => {
                const next = { ...current }
                delete next[tab]
                return next
              })
            }}
          />
        )}
      </main>

      {/* Bottom Navigation */}
      <nav className={`border-t mobile-nav ${currentThemeStyles.nav}`}>
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex justify-around">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex flex-col items-center py-3 px-4 transition-colors ${
                  activeTab === tab.id
                    ? 'text-blue-600'
                    : currentThemeStyles.inactiveTab
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
