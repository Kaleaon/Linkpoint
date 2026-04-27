'use client'

import { useEffect, useMemo, useState } from 'react'

export type ThemeCategory = 'Dark' | 'Light/Reading' | 'Accessibility' | 'Accent' | 'Metallic'

export interface ThemePreset {
  id: string
  name: string
  category: ThemeCategory
  swatches: [string, string, string]
}

export const THEME_PRESETS: ThemePreset[] = [
  { id: 'midnight', name: 'Midnight', category: 'Dark', swatches: ['#0F172A', '#1E293B', '#38BDF8'] },
  { id: 'ocean', name: 'Ocean', category: 'Dark', swatches: ['#082F49', '#0E7490', '#22D3EE'] },
  { id: 'nord', name: 'Nord', category: 'Dark', swatches: ['#2E3440', '#4C566A', '#88C0D0'] },
  { id: 'classic-dark', name: 'Classic Dark', category: 'Dark', swatches: ['#111827', '#374151', '#9CA3AF'] },
  { id: 'paper', name: 'Paper', category: 'Light/Reading', swatches: ['#FFFBEB', '#F3F4F6', '#6B7280'] },
  { id: 'cream', name: 'Cream', category: 'Light/Reading', swatches: ['#FFF7ED', '#FED7AA', '#9A3412'] },
  { id: 'classic-light', name: 'Classic Light', category: 'Light/Reading', swatches: ['#FFFFFF', '#E5E7EB', '#1F2937'] },
  { id: 'high-contrast', name: 'High Contrast', category: 'Accessibility', swatches: ['#000000', '#FFFFFF', '#FDE047'] },
  { id: 'soft-contrast', name: 'Soft Contrast', category: 'Accessibility', swatches: ['#111827', '#F9FAFB', '#2563EB'] },
  { id: 'gold', name: 'Gold', category: 'Accent', swatches: ['#451A03', '#F59E0B', '#FEF3C7'] },
  { id: 'violet-pop', name: 'Violet Pop', category: 'Accent', swatches: ['#4C1D95', '#8B5CF6', '#DDD6FE'] },
  { id: 'rose', name: 'Rose', category: 'Accent', swatches: ['#881337', '#FB7185', '#FFE4E6'] },
  { id: 'steel', name: 'Steel', category: 'Metallic', swatches: ['#1F2937', '#6B7280', '#D1D5DB'] },
  { id: 'silver', name: 'Silver', category: 'Metallic', swatches: ['#334155', '#94A3B8', '#E2E8F0'] },
]

const CATEGORIES: ThemeCategory[] = ['Dark', 'Light/Reading', 'Accessibility', 'Accent', 'Metallic']

export function getThemePresetById(themeId: string): ThemePreset | undefined {
  return THEME_PRESETS.find((preset) => preset.id === themeId)
}

interface ThemeSelectorProps {
  selectedThemeId: string
  onSelectTheme: (themeId: string) => void
  onUseForAllTabs?: (themeId: string) => void
  onUseForThisTabOnly?: (themeId: string) => void
  label?: string
}

export default function ThemeSelector({
  selectedThemeId,
  onSelectTheme,
  onUseForAllTabs,
  onUseForThisTabOnly,
  label = 'Theme Presets',
}: ThemeSelectorProps) {
  const [search, setSearch] = useState('')
  const [focusedThemeId, setFocusedThemeId] = useState(selectedThemeId)

  useEffect(() => {
    setFocusedThemeId(selectedThemeId)
  }, [selectedThemeId])

  const groupedPresets = useMemo(() => {
    const query = search.trim().toLowerCase()

    return CATEGORIES.map((category) => {
      const presets = THEME_PRESETS.filter((preset) => {
        if (preset.category !== category) {
          return false
        }

        if (!query) {
          return true
        }

        return (
          preset.name.toLowerCase().includes(query) ||
          preset.category.toLowerCase().includes(query)
        )
      })

      return { category, presets }
    }).filter((group) => group.presets.length > 0)
  }, [search])

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-4 space-y-4">
      <div>
        <p className="text-sm font-semibold text-gray-900">{label}</p>
        <input
          type="text"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search themes..."
          className="mt-2 input-field"
        />
      </div>

      <div className="space-y-3 max-h-72 overflow-y-auto pr-1">
        {groupedPresets.map((group) => (
          <div key={group.category} className="space-y-2">
            <h4 className="text-xs font-bold uppercase tracking-wide text-gray-500">
              {group.category}
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {group.presets.map((preset) => {
                const isSelected = preset.id === focusedThemeId

                return (
                  <button
                    key={preset.id}
                    type="button"
                    onClick={() => {
                      setFocusedThemeId(preset.id)
                      onSelectTheme(preset.id)
                    }}
                    className={`rounded-lg border px-3 py-2 text-left transition-colors ${
                      isSelected
                        ? 'border-blue-500 bg-blue-50'
                        : 'border-gray-200 hover:border-gray-300 hover:bg-gray-50'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-sm font-medium text-gray-900">{preset.name}</span>
                      <div className="flex items-center gap-1">
                        {preset.swatches.map((swatch) => (
                          <span
                            key={swatch}
                            className="h-3 w-3 rounded-full border border-black/10"
                            style={{ backgroundColor: swatch }}
                          />
                        ))}
                      </div>
                    </div>
                  </button>
                )
              })}
            </div>
          </div>
        ))}
      </div>

      {(onUseForAllTabs || onUseForThisTabOnly) && (
        <div className="flex flex-wrap gap-2 pt-1">
          {onUseForAllTabs && (
            <button
              type="button"
              className="btn-primary"
              onClick={() => onUseForAllTabs(focusedThemeId)}
            >
              Use for all tabs
            </button>
          )}
          {onUseForThisTabOnly && (
            <button
              type="button"
              className="btn-secondary"
              onClick={() => onUseForThisTabOnly(focusedThemeId)}
            >
              Use for this tab only
            </button>
          )}
        </div>
      )}
    </div>
  )
}
