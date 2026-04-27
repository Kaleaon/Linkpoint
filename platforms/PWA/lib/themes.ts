export type ThemePresetName =
  | 'Aurora Grid'
  | 'Sim Sunrise'
  | 'Cyber Noir'
  | 'Linden Bloom'
  | 'Builder’s Blueprint'
  | 'Event Neon'
  | 'Desert Sand'
  | 'Terminal Mono'

export interface ThemeTokens {
  appBackground: string
  panelBackground: string
  panelMuted: string
  panelElevated: string
  border: string
  textPrimary: string
  textSecondary: string
  textMuted: string
  accent: string
  accentStrong: string
  accentContrast: string
  danger: string
  worldCanvas: string
  worldGrid: string
  worldOverlay: string
  receivedBubble: string
  sentBubble: string
  inputBackground: string
  inputBorder: string
  inputFocus: string
}

export const THEME_PRESETS: Record<ThemePresetName, ThemeTokens> = {
  'Aurora Grid': {
    appBackground: '#070B1B',
    panelBackground: '#0D1430',
    panelMuted: '#101C40',
    panelElevated: '#14244F',
    border: '#27386F',
    textPrimary: '#E7EEFF',
    textSecondary: '#A8B7E6',
    textMuted: '#7D8CC1',
    accent: '#1CE6D6',
    accentStrong: '#8B5CFF',
    accentContrast: '#030712',
    danger: '#FF6B7A',
    worldCanvas: '#060A18',
    worldGrid: '#173261',
    worldOverlay: 'rgba(7, 11, 27, 0.65)',
    receivedBubble: '#192A5C',
    sentBubble: '#7A5CFF',
    inputBackground: '#0C1738',
    inputBorder: '#2E4688',
    inputFocus: '#1CE6D6',
  },
  'Sim Sunrise': {
    appBackground: '#FFF8EF',
    panelBackground: '#FFFFFF',
    panelMuted: '#FFF3E0',
    panelElevated: '#FFE8C7',
    border: '#F2D1A4',
    textPrimary: '#3F2B1D',
    textSecondary: '#6A4A30',
    textMuted: '#8E6A4A',
    accent: '#FFB347',
    accentStrong: '#5EB7FF',
    accentContrast: '#1D2A3A',
    danger: '#D94F3D',
    worldCanvas: '#FFF3E6',
    worldGrid: '#FFD9A0',
    worldOverlay: 'rgba(255, 244, 225, 0.78)',
    receivedBubble: '#FFE6C4',
    sentBubble: '#7CC7FF',
    inputBackground: '#FFFDF8',
    inputBorder: '#F4C995',
    inputFocus: '#5EB7FF',
  },
  'Cyber Noir': {
    appBackground: '#0A0D12',
    panelBackground: '#11161D',
    panelMuted: '#161D26',
    panelElevated: '#1D2631',
    border: '#2E3C4F',
    textPrimary: '#E5EDF5',
    textSecondary: '#A3B5C7',
    textMuted: '#7D90A4',
    accent: '#00E7FF',
    accentStrong: '#00A5D8',
    accentContrast: '#01161D',
    danger: '#FF5E7D',
    worldCanvas: '#080C12',
    worldGrid: '#1E2D3D',
    worldOverlay: 'rgba(10, 13, 18, 0.72)',
    receivedBubble: '#1A2430',
    sentBubble: '#008FBF',
    inputBackground: '#111923',
    inputBorder: '#2C4158',
    inputFocus: '#00E7FF',
  },
  'Linden Bloom': {
    appBackground: '#F5FAF4',
    panelBackground: '#FFFFFF',
    panelMuted: '#EDF5EA',
    panelElevated: '#E2EFDD',
    border: '#C7DCC1',
    textPrimary: '#203321',
    textSecondary: '#39523A',
    textMuted: '#5B765C',
    accent: '#3FBF7F',
    accentStrong: '#2D8E62',
    accentContrast: '#F4FFF9',
    danger: '#C74545',
    worldCanvas: '#EAF4E5',
    worldGrid: '#BFD5BE',
    worldOverlay: 'rgba(237, 245, 234, 0.76)',
    receivedBubble: '#DCEAD8',
    sentBubble: '#3AA775',
    inputBackground: '#F8FCF6',
    inputBorder: '#B8D3B0',
    inputFocus: '#3FBF7F',
  },
  'Builder’s Blueprint': {
    appBackground: '#E7EEF5',
    panelBackground: '#F5F8FC',
    panelMuted: '#E2E9F2',
    panelElevated: '#D7E0EB',
    border: '#B4C2D3',
    textPrimary: '#223447',
    textSecondary: '#42596F',
    textMuted: '#61768A',
    accent: '#5F84A8',
    accentStrong: '#3F6587',
    accentContrast: '#F2F8FF',
    danger: '#B24A4A',
    worldCanvas: '#DCE6F1',
    worldGrid: '#AEC0D4',
    worldOverlay: 'rgba(220, 230, 241, 0.78)',
    receivedBubble: '#D2DDE9',
    sentBubble: '#4E7599',
    inputBackground: '#F7FAFD',
    inputBorder: '#A8BBCE',
    inputFocus: '#5F84A8',
  },
  'Event Neon': {
    appBackground: '#100419',
    panelBackground: '#1A0B29',
    panelMuted: '#2A0F3F',
    panelElevated: '#371256',
    border: '#5A2A88',
    textPrimary: '#F8EFFF',
    textSecondary: '#D3B6F7',
    textMuted: '#B28CD6',
    accent: '#FF4FD8',
    accentStrong: '#00E5FF',
    accentContrast: '#140022',
    danger: '#FF6A9F',
    worldCanvas: '#12051D',
    worldGrid: '#3B1A5D',
    worldOverlay: 'rgba(16, 4, 25, 0.68)',
    receivedBubble: '#301148',
    sentBubble: '#00CFE8',
    inputBackground: '#1E0B31',
    inputBorder: '#65319A',
    inputFocus: '#00E5FF',
  },
  'Desert Sand': {
    appBackground: '#F7EFE3',
    panelBackground: '#FFF9F0',
    panelMuted: '#F0E2CE',
    panelElevated: '#E6D2B6',
    border: '#CDB28F',
    textPrimary: '#4A3724',
    textSecondary: '#6C5136',
    textMuted: '#8A6A49',
    accent: '#C67A3D',
    accentStrong: '#9E5D2E',
    accentContrast: '#FFF7EE',
    danger: '#B34A3E',
    worldCanvas: '#EDE0CC',
    worldGrid: '#C7AC86',
    worldOverlay: 'rgba(247, 239, 227, 0.78)',
    receivedBubble: '#E4D2B7',
    sentBubble: '#B66B36',
    inputBackground: '#FFFAF3',
    inputBorder: '#CFAF84',
    inputFocus: '#C67A3D',
  },
  'Terminal Mono': {
    appBackground: '#0D0D0D',
    panelBackground: '#171717',
    panelMuted: '#1F1F1F',
    panelElevated: '#292929',
    border: '#3C3C3C',
    textPrimary: '#F2F2F2',
    textSecondary: '#C8C8C8',
    textMuted: '#A1A1A1',
    accent: '#A3E635',
    accentStrong: '#84CC16',
    accentContrast: '#0A0A0A',
    danger: '#F87171',
    worldCanvas: '#0B0B0B',
    worldGrid: '#2E2E2E',
    worldOverlay: 'rgba(13, 13, 13, 0.78)',
    receivedBubble: '#252525',
    sentBubble: '#3F3F46',
    inputBackground: '#141414',
    inputBorder: '#454545',
    inputFocus: '#A3E635',
  },
}

export const DEFAULT_THEME_PRESET: ThemePresetName = 'Aurora Grid'

export const getThemeTokens = (preset: ThemePresetName = DEFAULT_THEME_PRESET) => THEME_PRESETS[preset]
