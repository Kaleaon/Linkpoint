'use client'

import { useState, useEffect } from 'react'
import LoginPage from '@/components/LoginPage'
import MainApp from '@/components/MainApp'

export default function Home() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [user, setUser] = useState<any>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    // Check for existing session
    const savedUser = localStorage.getItem('linkpoint_user')
    if (savedUser) {
      try {
        const userData = JSON.parse(savedUser)
        setUser(userData)
        setIsAuthenticated(true)
      } catch (error) {
        console.error('Error loading saved user:', error)
        localStorage.removeItem('linkpoint_user')
      }
    }
    setIsLoading(false)
  }, [])

  const handleLogin = async (credentials: {
    username: string
    password: string
    firstName: string
    lastName: string
    grid: string
  }) => {
    // Simulate authentication
    // In production, this would make an XMLRPC call to the grid
    const userData = {
      username: credentials.username,
      firstName: credentials.firstName,
      lastName: credentials.lastName,
      fullName: `${credentials.firstName} ${credentials.lastName}`,
      grid: credentials.grid,
      loginTime: new Date().toISOString(),
    }

    localStorage.setItem('linkpoint_user', JSON.stringify(userData))
    setUser(userData)
    setIsAuthenticated(true)
  }

  const handleLogout = () => {
    localStorage.removeItem('linkpoint_user')
    setUser(null)
    setIsAuthenticated(false)
  }

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-500 to-purple-600">
        <div className="spinner"></div>
      </div>
    )
  }

  return (
    <main className="min-h-screen">
      {isAuthenticated ? (
        <MainApp user={user} onLogout={handleLogout} />
      ) : (
        <LoginPage onLogin={handleLogin} />
      )}
    </main>
  )
}