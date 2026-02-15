'use client'

import { getThemeTokens } from '../lib/themes'

interface ProfileViewProps {
  user: any
  onLogout: () => void
}

export default function ProfileView({ user, onLogout }: ProfileViewProps) {
  const theme = getThemeTokens()

  return (
    <div className="h-full overflow-y-auto" style={{ backgroundColor: theme.appBackground }}>
      <div className="max-w-2xl mx-auto p-4 space-y-4">
        {/* Profile Header */}
        <div className="rounded-lg p-6" style={{ backgroundColor: theme.panelBackground, border: `1px solid ${theme.border}` }}>
          <div className="flex items-center space-x-4">
            <div
              className="w-20 h-20 rounded-full flex items-center justify-center text-3xl font-bold"
              style={{ background: `linear-gradient(135deg, ${theme.accent}, ${theme.accentStrong})`, color: theme.accentContrast }}
            >
              {user.firstName[0]}{user.lastName[0]}
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-bold" style={{ color: theme.textPrimary }}>{user.fullName}</h2>
              <p style={{ color: theme.textSecondary }}>@{user.username}</p>
              <p className="text-sm mt-1" style={{ color: theme.textMuted }}>
                Member since {new Date(user.loginTime).toLocaleDateString()}
              </p>
            </div>
          </div>
        </div>

        {/* Account Info */}
        <div className="rounded-lg p-6" style={{ backgroundColor: theme.panelBackground, border: `1px solid ${theme.border}` }}>
          <h3 className="text-lg font-semibold mb-4" style={{ color: theme.textPrimary }}>Account Information</h3>
          <div className="space-y-3">
            <div className="flex justify-between py-2 border-b" style={{ borderColor: theme.border }}>
              <span style={{ color: theme.textSecondary }}>First Name</span>
              <span className="font-medium" style={{ color: theme.textPrimary }}>{user.firstName}</span>
            </div>
            <div className="flex justify-between py-2 border-b" style={{ borderColor: theme.border }}>
              <span style={{ color: theme.textSecondary }}>Last Name</span>
              <span className="font-medium" style={{ color: theme.textPrimary }}>{user.lastName}</span>
            </div>
            <div className="flex justify-between py-2 border-b" style={{ borderColor: theme.border }}>
              <span style={{ color: theme.textSecondary }}>Username</span>
              <span className="font-medium" style={{ color: theme.textPrimary }}>{user.username}</span>
            </div>
            <div className="flex justify-between py-2">
              <span style={{ color: theme.textSecondary }}>Current Grid</span>
              <span className="font-medium" style={{ color: theme.textPrimary }}>{user.grid}</span>
            </div>
          </div>
        </div>

        {/* Settings */}
        <div className="rounded-lg p-6" style={{ backgroundColor: theme.panelBackground, border: `1px solid ${theme.border}` }}>
          <h3 className="text-lg font-semibold mb-4" style={{ color: theme.textPrimary }}>Settings</h3>
          <div className="space-y-2">
            {['Edit Profile', 'Preferences', 'Graphics Settings', 'Privacy'].map((item) => (
              <button
                key={item}
                className="w-full text-left px-4 py-3 rounded-lg transition-colors flex items-center justify-between"
                style={{ backgroundColor: theme.panelMuted, border: `1px solid ${theme.border}` }}
              >
                <span className="font-medium" style={{ color: theme.textPrimary }}>{item}</span>
                <svg className="w-5 h-5" style={{ color: theme.textMuted }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                </svg>
              </button>
            ))}
          </div>
        </div>

        {/* Statistics */}
        <div className="rounded-lg p-6" style={{ backgroundColor: theme.panelBackground, border: `1px solid ${theme.border}` }}>
          <h3 className="text-lg font-semibold mb-4" style={{ color: theme.textPrimary }}>Statistics</h3>
          <div className="grid grid-cols-2 gap-4">
            {[
              { value: '127', label: 'Friends' },
              { value: '45', label: 'Groups' },
              { value: '892', label: 'Inventory Items' },
              { value: '23', label: 'Landmarks' },
            ].map((stat) => (
              <div key={stat.label} className="text-center p-4 rounded-lg" style={{ backgroundColor: theme.panelMuted, border: `1px solid ${theme.border}` }}>
                <div className="text-3xl font-bold" style={{ color: theme.accentStrong }}>{stat.value}</div>
                <div className="text-sm mt-1" style={{ color: theme.textSecondary }}>{stat.label}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Logout Button */}
        <div className="rounded-lg p-6" style={{ backgroundColor: theme.panelBackground, border: `1px solid ${theme.border}` }}>
          <button
            onClick={onLogout}
            className="w-full py-3 text-white font-semibold rounded-lg transition-colors"
            style={{ backgroundColor: theme.danger }}
          >
            Logout
          </button>
        </div>
      </div>
    </div>
  )
}
