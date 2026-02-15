'use client'

import { useState } from 'react'
import {
  ACCESSIBILITY_PRESETS,
  COMFORT_PRESETS,
  DEFAULT_PRESET,
  type AccessibilityPresetId,
  resolveAccessibilityTheme,
} from '../lib/themes'

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

const FOLDER_TO_TYPE: Record<string, string> = {
  Animations: 'Animation',
  'Body Parts': 'Body Part',
  Clothing: 'Clothing',
  Gestures: 'Gesture',
  Landmarks: 'Landmark',
  Notecards: 'Notecard',
  Objects: 'Object',
  Scripts: 'Script',
  Sounds: 'Sound',
  Textures: 'Texture',
}

export default function InventoryView() {
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedFolder, setSelectedFolder] = useState<string | null>(null)
  const [activePreset, setActivePreset] = useState<AccessibilityPresetId>(DEFAULT_PRESET)
  const [lastNonComfortPreset, setLastNonComfortPreset] = useState<AccessibilityPresetId>(DEFAULT_PRESET)
  const [reducedGlare, setReducedGlare] = useState(false)

  const filteredItems = INVENTORY_ITEMS.filter((item) => {
    const folderType = selectedFolder ? FOLDER_TO_TYPE[selectedFolder] : null
    const matchesFolder = folderType ? item.type === folderType : true
    const matchesSearch = item.name.toLowerCase().includes(searchQuery.toLowerCase())

    return matchesFolder && matchesSearch
  })

  const isComfortMode = COMFORT_PRESETS.includes(activePreset)
  const theme = resolveAccessibilityTheme(activePreset, reducedGlare)

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

  return (
    <div className="h-full flex flex-col" style={{ backgroundColor: theme.colors.appBackground, color: theme.colors.textPrimary }}>
      <div className="p-3 border-b space-y-2" style={{ borderColor: theme.colors.border, backgroundColor: theme.colors.panelMutedBackground }}>
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

      <div className="p-4 border-b" style={{ borderColor: theme.colors.border, backgroundColor: theme.colors.panelBackground }}>
        <input
          type="text"
          placeholder="Search inventory..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-full px-4 py-2 rounded-lg border focus:outline-none"
          style={{ backgroundColor: theme.colors.panelBackground, borderColor: theme.colors.border, color: theme.colors.textPrimary }}
        />
      </div>

      <div className="flex-1 overflow-y-auto" style={{ backgroundColor: theme.colors.panelBackground }}>
        <div className="p-4">
          <h2 className="text-lg font-semibold mb-3">Recent Items</h2>
          <div className="space-y-2">
            {filteredItems.slice(0, 3).map((item) => (
              <div
                key={item.id}
                className="flex items-center p-3 rounded-lg cursor-pointer transition-colors border"
                style={{ backgroundColor: theme.colors.listItemBackground, borderColor: theme.colors.border }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.backgroundColor = theme.colors.listItemHoverBackground
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.backgroundColor = theme.colors.listItemBackground
                }}
              >
                <span className="text-2xl mr-3">{item.icon}</span>
                <div className="flex-1">
                  <div className="font-medium" style={{ color: theme.colors.textPrimary }}>{item.name}</div>
                  <div className="text-sm" style={{ color: theme.colors.textSecondary }}>{item.type}</div>
                </div>
                <svg
                  className="w-5 h-5"
                  style={{ color: theme.colors.textSecondary }}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 5l7 7-7 7"
                  />
                </svg>
              </div>
            ))}
          </div>
        </div>

        <div className="p-4">
          <h2 className="text-lg font-semibold mb-3">Folders</h2>
          <div className="grid grid-cols-2 gap-3">
            {FOLDERS.map((folder) => (
              <div
                key={folder.name}
                onClick={() => setSelectedFolder(folder.name)}
                className="flex flex-col items-center p-4 rounded-lg cursor-pointer transition-colors border"
                style={{
                  backgroundColor: selectedFolder === folder.name ? theme.colors.panelMutedBackground : theme.colors.cardBackground,
                  borderColor: theme.colors.border,
                }}
              >
                <span className="text-3xl mb-2">{folder.icon}</span>
                <div className="font-medium text-sm text-center" style={{ color: theme.colors.textPrimary }}>
                  {folder.name}
                </div>
                <div className="text-xs mt-1" style={{ color: theme.colors.textSecondary }}>
                  {folder.count} items
                </div>
              </div>
            ))}
          </div>
        </div>

        {searchQuery && (
          <div className="p-4">
            <h2 className="text-lg font-semibold mb-3">
              Search Results ({filteredItems.length})
            </h2>
            <div className="space-y-2">
              {filteredItems.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center p-3 rounded-lg cursor-pointer border"
                  style={{ backgroundColor: theme.colors.listItemBackground, borderColor: theme.colors.border }}
                >
                  <span className="text-2xl mr-3">{item.icon}</span>
                  <div className="flex-1">
                    <div className="font-medium" style={{ color: theme.colors.textPrimary }}>{item.name}</div>
                    <div className="text-sm" style={{ color: theme.colors.textSecondary }}>{item.type}</div>
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
