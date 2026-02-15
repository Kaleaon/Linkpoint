'use client'

import { useState } from 'react'

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

  const filteredItems = INVENTORY_ITEMS.filter((item) => {
    const folderType = selectedFolder ? FOLDER_TO_TYPE[selectedFolder] : null
    const matchesFolder = folderType ? item.type === folderType : true
    const matchesSearch = item.name.toLowerCase().includes(searchQuery.toLowerCase())

    return matchesFolder && matchesSearch
  })

  return (
    <div className="h-full flex flex-col bg-white">
      {/* Search Bar */}
      <div className="p-4 border-b border-gray-200">
        <input
          type="text"
          placeholder="Search inventory..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="input-field"
        />
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto">
        {/* Recent Items */}
        <div className="p-4">
          <h2 className="text-lg font-semibold text-gray-900 mb-3">Recent Items</h2>
          <div className="space-y-2">
            {filteredItems.slice(0, 3).map((item) => (
              <div
                key={item.id}
                className="flex items-center p-3 bg-gray-50 rounded-lg hover:bg-gray-100 cursor-pointer transition-colors"
              >
                <span className="text-2xl mr-3">{item.icon}</span>
                <div className="flex-1">
                  <div className="font-medium text-gray-900">{item.name}</div>
                  <div className="text-sm text-gray-600">{item.type}</div>
                </div>
                <svg
                  className="w-5 h-5 text-gray-400"
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

        {/* Folders */}
        <div className="p-4">
          <h2 className="text-lg font-semibold text-gray-900 mb-3">Folders</h2>
          <div className="grid grid-cols-2 gap-3">
            {FOLDERS.map((folder) => (
              <div
                key={folder.name}
                onClick={() => setSelectedFolder(folder.name)}
                className={`flex flex-col items-center p-4 rounded-lg cursor-pointer transition-colors ${
                  selectedFolder === folder.name
                    ? 'bg-blue-100 ring-2 ring-blue-500'
                    : 'bg-gray-50 hover:bg-gray-100'
                }`}
              >
                <span className="text-3xl mb-2">{folder.icon}</span>
                <div className="font-medium text-gray-900 text-sm text-center">
                  {folder.name}
                </div>
                <div className="text-xs text-gray-600 mt-1">
                  {folder.count} items
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* All Items */}
        {searchQuery && (
          <div className="p-4">
            <h2 className="text-lg font-semibold text-gray-900 mb-3">
              Search Results ({filteredItems.length})
            </h2>
            <div className="space-y-2">
              {filteredItems.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center p-3 bg-gray-50 rounded-lg hover:bg-gray-100 cursor-pointer transition-colors"
                >
                  <span className="text-2xl mr-3">{item.icon}</span>
                  <div className="flex-1">
                    <div className="font-medium text-gray-900">{item.name}</div>
                    <div className="text-sm text-gray-600">{item.type}</div>
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
