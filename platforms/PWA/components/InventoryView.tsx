'use client'

import { useState } from 'react'
import { getThemeTokens } from '../lib/themes'

interface InventoryItem {
  id: string
  name: string
  type: string
  icon: string
}

const INVENTORY_ITEMS: InventoryItem[] = [
  { id: '1', name: 'My Outfit', type: 'Clothing', icon: '👕' },
  { id: '2', name: 'Favorite Place', type: 'Landmark', icon: '📍' },
  { id: '3', name: 'Photo Album', type: 'Texture', icon: '🖼️' },
  { id: '4', name: 'Dance Animation', type: 'Animation', icon: '💃' },
  { id: '5', name: 'Welcome Script', type: 'Script', icon: '📜' },
  { id: '6', name: 'Building Blocks', type: 'Object', icon: '🧱' },
  { id: '7', name: 'Avatar Skin', type: 'Body Part', icon: '👤' },
  { id: '8', name: 'Sound Effect', type: 'Sound', icon: '🔊' },
]

const FOLDERS = [
  { name: 'Animations', icon: '💃', count: 12 },
  { name: 'Body Parts', icon: '👤', count: 8 },
  { name: 'Clothing', icon: '👕', count: 24 },
  { name: 'Gestures', icon: '👋', count: 15 },
  { name: 'Landmarks', icon: '📍', count: 32 },
  { name: 'Notecards', icon: '📝', count: 18 },
  { name: 'Objects', icon: '🧱', count: 45 },
  { name: 'Scripts', icon: '📜', count: 28 },
  { name: 'Sounds', icon: '🔊', count: 16 },
  { name: 'Textures', icon: '🖼️', count: 67 },
]

export default function InventoryView() {
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedFolder, setSelectedFolder] = useState<string | null>(null)
  const theme = getThemeTokens()

  const filteredItems = INVENTORY_ITEMS.filter(item =>
    item.name.toLowerCase().includes(searchQuery.toLowerCase())
  )

  return (
    <div className="h-full flex flex-col" style={{ backgroundColor: theme.panelBackground }}>
      {/* Search Bar */}
      <div className="p-4 border-b" style={{ borderColor: theme.border }}>
        <input
          type="text"
          placeholder="Search inventory..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-full px-4 py-2 rounded-lg focus:outline-none focus:ring-2"
          style={{
            backgroundColor: theme.inputBackground,
            border: `1px solid ${theme.inputBorder}`,
            color: theme.textPrimary,
            boxShadow: `0 0 0 0 ${theme.inputFocus}`,
          }}
        />
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto">
        {/* Recent Items */}
        <div className="p-4">
          <h2 className="text-lg font-semibold mb-3" style={{ color: theme.textPrimary }}>Recent Items</h2>
          <div className="space-y-2">
            {filteredItems.slice(0, 3).map((item) => (
              <div
                key={item.id}
                className="flex items-center p-3 rounded-lg cursor-pointer transition-colors"
                style={{ backgroundColor: theme.panelMuted, border: `1px solid ${theme.border}` }}
              >
                <span className="text-2xl mr-3">{item.icon}</span>
                <div className="flex-1">
                  <div className="font-medium" style={{ color: theme.textPrimary }}>{item.name}</div>
                  <div className="text-sm" style={{ color: theme.textSecondary }}>{item.type}</div>
                </div>
                <svg
                  className="w-5 h-5"
                  style={{ color: theme.textMuted }}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
              </div>
            ))}
          </div>
        </div>

        {/* Folders */}
        <div className="p-4">
          <h2 className="text-lg font-semibold mb-3" style={{ color: theme.textPrimary }}>Folders</h2>
          {selectedFolder && (
            <div className="text-xs mb-3" style={{ color: theme.accent }}>Selected: {selectedFolder}</div>
          )}
          <div className="grid grid-cols-2 gap-3">
            {FOLDERS.map((folder) => (
              <button
                key={folder.name}
                type="button"
                onClick={() => setSelectedFolder(folder.name)}
                className="flex flex-col items-center p-4 rounded-lg cursor-pointer transition-colors"
                style={{
                  backgroundColor: selectedFolder === folder.name ? theme.panelElevated : theme.panelMuted,
                  border: `1px solid ${selectedFolder === folder.name ? theme.accent : theme.border}`,
                }}
              >
                <span className="text-3xl mb-2">{folder.icon}</span>
                <div className="font-medium text-sm text-center" style={{ color: theme.textPrimary }}>
                  {folder.name}
                </div>
                <div className="text-xs mt-1" style={{ color: theme.textSecondary }}>
                  {folder.count} items
                </div>
              </button>
            ))}
          </div>
        </div>

        {/* All Items */}
        {searchQuery && (
          <div className="p-4">
            <h2 className="text-lg font-semibold mb-3" style={{ color: theme.textPrimary }}>
              Search Results ({filteredItems.length})
            </h2>
            <div className="space-y-2">
              {filteredItems.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center p-3 rounded-lg cursor-pointer transition-colors"
                  style={{ backgroundColor: theme.panelMuted, border: `1px solid ${theme.border}` }}
                >
                  <span className="text-2xl mr-3">{item.icon}</span>
                  <div className="flex-1">
                    <div className="font-medium" style={{ color: theme.textPrimary }}>{item.name}</div>
                    <div className="text-sm" style={{ color: theme.textSecondary }}>{item.type}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
