'use client'

import ThemeSelector, { getThemePresetById } from './ThemeSelector'

type Tab = 'world' | 'inventory' | 'chat' | 'profile'

type TabThemeOverrides = Partial<Record<Tab, string>>

interface ProfileViewProps {
  user: any
  onLogout: () => void
  globalThemeId: string
  onGlobalThemeChange: (themeId: string) => void
  tabThemeOverrides: TabThemeOverrides
  onTabThemeChange: (tab: Tab, themeId: string) => void
  onClearTabTheme: (tab: Tab) => void
}

const tabLabels: Record<Tab, string> = {
  world: 'World',
  chat: 'Chat',
  inventory: 'Inventory',
  profile: 'Profile',
}

export default function ProfileView({
  user,
  onLogout,
  globalThemeId,
  onGlobalThemeChange,
  tabThemeOverrides,
  onTabThemeChange,
  onClearTabTheme,
}: ProfileViewProps) {
  const tabOrder: Tab[] = ['world', 'chat', 'inventory', 'profile']

  return (
    <div className="h-full overflow-y-auto bg-gray-50">
      <div className="max-w-2xl mx-auto p-4 space-y-4">
        {/* Profile Header */}
        <div className="card">
          <div className="flex items-center space-x-4">
            <div className="w-20 h-20 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white text-3xl font-bold">
              {user.firstName[0]}{user.lastName[0]}
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-bold text-gray-900">{user.fullName}</h2>
              <p className="text-gray-600">@{user.username}</p>
              <p className="text-sm text-gray-500 mt-1">
                Member since {new Date(user.loginTime).toLocaleDateString()}
              </p>
            </div>
          </div>
        </div>

        {/* Account Info */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Account Information</h3>
          <div className="space-y-3">
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">First Name</span>
              <span className="font-medium text-gray-900">{user.firstName}</span>
            </div>
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">Last Name</span>
              <span className="font-medium text-gray-900">{user.lastName}</span>
            </div>
            <div className="flex justify-between py-2 border-b border-gray-100">
              <span className="text-gray-600">Username</span>
              <span className="font-medium text-gray-900">{user.username}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-gray-600">Current Grid</span>
              <span className="font-medium text-gray-900">{user.grid}</span>
            </div>
          </div>
        </div>

        {/* Theme Settings */}
        <div className="card space-y-4">
          <div>
            <h3 className="text-lg font-semibold text-gray-900">Theme Settings</h3>
            <p className="text-sm text-gray-600 mt-1">
              Set a global theme and optionally override it for specific tabs.
            </p>
          </div>

          <ThemeSelector
            label="Global theme"
            selectedThemeId={globalThemeId}
            onSelectTheme={onGlobalThemeChange}
          />

          <div className="space-y-3">
            <h4 className="text-sm font-semibold text-gray-900">Per-tab overrides</h4>
            {tabOrder.map((tab) => {
              const overrideThemeId = tabThemeOverrides[tab]
              const overrideThemeName = overrideThemeId
                ? getThemePresetById(overrideThemeId)?.name || overrideThemeId
                : 'Using global theme'

              return (
                <div key={tab} className="rounded-lg border border-gray-200 p-3 bg-white space-y-2">
                  <div className="flex items-center justify-between gap-2">
                    <div>
                      <p className="text-sm font-semibold text-gray-900">{tabLabels[tab]}</p>
                      <p className="text-xs text-gray-500">{overrideThemeName}</p>
                    </div>
                    {overrideThemeId && (
                      <button
                        type="button"
                        onClick={() => onClearTabTheme(tab)}
                        className="text-xs font-medium text-blue-600 hover:text-blue-700"
                      >
                        Use global theme
                      </button>
                    )}
                  </div>
                  <ThemeSelector
                    label={`${tabLabels[tab]} override`}
                    selectedThemeId={overrideThemeId || globalThemeId}
                    onSelectTheme={(themeId) => onTabThemeChange(tab, themeId)}
                  />
                </div>
              )
            })}
          </div>
        </div>

        {/* Settings */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Settings</h3>
          <div className="space-y-2">
            <button className="w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors flex items-center justify-between">
              <span className="font-medium text-gray-900">Edit Profile</span>
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
            </button>
            <button className="w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors flex items-center justify-between">
              <span className="font-medium text-gray-900">Preferences</span>
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
            </button>
            <button className="w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors flex items-center justify-between">
              <span className="font-medium text-gray-900">Graphics Settings</span>
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
            </button>
            <button className="w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 rounded-lg transition-colors flex items-center justify-between">
              <span className="font-medium text-gray-900">Privacy</span>
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
            </button>
          </div>
        </div>

        {/* Statistics */}
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Statistics</h3>
          <div className="grid grid-cols-2 gap-4">
            <div className="text-center p-4 bg-blue-50 rounded-lg">
              <div className="text-3xl font-bold text-blue-600">127</div>
              <div className="text-sm text-gray-600 mt-1">Friends</div>
            </div>
            <div className="text-center p-4 bg-purple-50 rounded-lg">
              <div className="text-3xl font-bold text-purple-600">45</div>
              <div className="text-sm text-gray-600 mt-1">Groups</div>
            </div>
            <div className="text-center p-4 bg-green-50 rounded-lg">
              <div className="text-3xl font-bold text-green-600">892</div>
              <div className="text-sm text-gray-600 mt-1">Inventory Items</div>
            </div>
            <div className="text-center p-4 bg-orange-50 rounded-lg">
              <div className="text-3xl font-bold text-orange-600">23</div>
              <div className="text-sm text-gray-600 mt-1">Landmarks</div>
            </div>
          </div>
        </div>

        {/* Logout Button */}
        <div className="card">
          <button
            onClick={onLogout}
            className="w-full py-3 bg-red-600 hover:bg-red-700 text-white font-semibold rounded-lg transition-colors"
          >
            Logout
          </button>
        </div>
      </div>
    </div>
  )
}
