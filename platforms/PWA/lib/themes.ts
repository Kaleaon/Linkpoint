export type AccessibilityPresetId = 'eInk' | 'eInkDark' | 'sepia' | 'nightRed'

export interface AccessibilityTheme {
  id: AccessibilityPresetId
  name: string
  description: string
  isDark: boolean
  colors: {
    appBackground: string
    panelBackground: string
    panelMutedBackground: string
    textPrimary: string
    textSecondary: string
    border: string
    accent: string
    accentText: string
    sentBubbleBackground: string
    sentBubbleText: string
    receivedBubbleBackground: string
    receivedBubbleText: string
    timestampSent: string
    timestampReceived: string
    listItemBackground: string
    listItemHoverBackground: string
    cardBackground: string
  }
  reducedGlareColors: Partial<AccessibilityTheme['colors']>
}

export const ACCESSIBILITY_PRESETS: Record<AccessibilityPresetId, AccessibilityTheme> = {
  eInk: {
    id: 'eInk',
    name: 'E-Ink',
    description: 'High contrast light',
    isDark: false,
    colors: {
      appBackground: '#f6f5f0',
      panelBackground: '#ffffff',
      panelMutedBackground: '#f0eee7',
      textPrimary: '#111111',
      textSecondary: '#2e2e2e',
      border: '#252525',
      accent: '#0f172a',
      accentText: '#ffffff',
      sentBubbleBackground: '#111111',
      sentBubbleText: '#ffffff',
      receivedBubbleBackground: '#e7e5de',
      receivedBubbleText: '#101010',
      timestampSent: '#d1d5db',
      timestampReceived: '#3f3f46',
      listItemBackground: '#f8f7f2',
      listItemHoverBackground: '#ebe8df',
      cardBackground: '#ffffff',
    },
    reducedGlareColors: {
      panelBackground: '#f8f6f1',
      panelMutedBackground: '#efecdf',
      listItemBackground: '#f4f1e7',
      listItemHoverBackground: '#e9e3d2',
      border: '#4b4b4b',
    },
  },
  eInkDark: {
    id: 'eInkDark',
    name: 'E-Ink Dark',
    description: 'High contrast dark',
    isDark: true,
    colors: {
      appBackground: '#0b0b0b',
      panelBackground: '#111111',
      panelMutedBackground: '#161616',
      textPrimary: '#f5f5f5',
      textSecondary: '#d4d4d4',
      border: '#d4d4d4',
      accent: '#f5f5f5',
      accentText: '#111111',
      sentBubbleBackground: '#f3f4f6',
      sentBubbleText: '#101010',
      receivedBubbleBackground: '#1f1f1f',
      receivedBubbleText: '#f5f5f5',
      timestampSent: '#3f3f46',
      timestampReceived: '#a1a1aa',
      listItemBackground: '#171717',
      listItemHoverBackground: '#232323',
      cardBackground: '#101010',
    },
    reducedGlareColors: {
      panelBackground: '#151515',
      panelMutedBackground: '#1a1a1a',
      listItemBackground: '#1c1c1c',
      listItemHoverBackground: '#2a2a2a',
      border: '#a3a3a3',
    },
  },
  sepia: {
    id: 'sepia',
    name: 'Sepia',
    description: 'Warm reading',
    isDark: false,
    colors: {
      appBackground: '#f1e7d0',
      panelBackground: '#f8f1df',
      panelMutedBackground: '#efe1c4',
      textPrimary: '#2f1e0d',
      textSecondary: '#5c3d20',
      border: '#7a5a36',
      accent: '#5b3a1f',
      accentText: '#fff6e9',
      sentBubbleBackground: '#6b4423',
      sentBubbleText: '#fff7eb',
      receivedBubbleBackground: '#e8d7b5',
      receivedBubbleText: '#2f1e0d',
      timestampSent: '#f3dcc2',
      timestampReceived: '#6b4a2c',
      listItemBackground: '#f4e8ce',
      listItemHoverBackground: '#ead9b8',
      cardBackground: '#f8f1df',
    },
    reducedGlareColors: {
      panelBackground: '#f3e8d0',
      panelMutedBackground: '#e9dbbc',
      accent: '#65492f',
      listItemBackground: '#eee0c3',
      listItemHoverBackground: '#e3d1ad',
      border: '#8d7353',
    },
  },
  nightRed: {
    id: 'nightRed',
    name: 'Night Red',
    description: 'Low-blue-light',
    isDark: true,
    colors: {
      appBackground: '#110607',
      panelBackground: '#19090a',
      panelMutedBackground: '#230d0f',
      textPrimary: '#fbdcde',
      textSecondary: '#e6b8bc',
      border: '#b56a73',
      accent: '#c45968',
      accentText: '#fff4f5',
      sentBubbleBackground: '#d16b77',
      sentBubbleText: '#fff6f7',
      receivedBubbleBackground: '#2a1215',
      receivedBubbleText: '#fcdce0',
      timestampSent: '#ffe3e6',
      timestampReceived: '#d1969d',
      listItemBackground: '#210e11',
      listItemHoverBackground: '#311417',
      cardBackground: '#1a090a',
    },
    reducedGlareColors: {
      panelBackground: '#211113',
      panelMutedBackground: '#2b1518',
      accent: '#b46974',
      listItemBackground: '#291417',
      listItemHoverBackground: '#381d21',
      border: '#9b6b71',
    },
  },
}

export const DEFAULT_PRESET: AccessibilityPresetId = 'eInk'

export const COMFORT_PRESETS: AccessibilityPresetId[] = ['sepia', 'nightRed']

export function resolveAccessibilityTheme(
  preset: AccessibilityPresetId,
  reducedGlare: boolean
): AccessibilityTheme {
  const theme = ACCESSIBILITY_PRESETS[preset] ?? ACCESSIBILITY_PRESETS[DEFAULT_PRESET]
  if (!reducedGlare) return theme

  return {
    ...theme,
    colors: {
      ...theme.colors,
      ...theme.reducedGlareColors,
    },
  }
}
