'use client'

import { useEffect, useRef } from 'react'
import { getThemeTokens } from '../lib/themes'

export default function WorldView() {
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const theme = getThemeTokens()

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const ctx = canvas.getContext('2d')
    if (!ctx) return

    // Set canvas size
    canvas.width = canvas.offsetWidth
    canvas.height = canvas.offsetHeight

    // Draw placeholder 3D scene
    const drawScene = () => {
      // Clear canvas
      ctx.fillStyle = theme.worldCanvas
      ctx.fillRect(0, 0, canvas.width, canvas.height)

      // Draw grid
      ctx.strokeStyle = theme.worldGrid
      ctx.lineWidth = 1

      const gridSize = 50
      for (let x = 0; x < canvas.width; x += gridSize) {
        ctx.beginPath()
        ctx.moveTo(x, 0)
        ctx.lineTo(x, canvas.height)
        ctx.stroke()
      }

      for (let y = 0; y < canvas.height; y += gridSize) {
        ctx.beginPath()
        ctx.moveTo(0, y)
        ctx.lineTo(canvas.width, y)
        ctx.stroke()
      }

      // Draw placeholder text
      ctx.fillStyle = theme.textPrimary
      ctx.font = '24px sans-serif'
      ctx.textAlign = 'center'
      ctx.fillText('3D World View', canvas.width / 2, canvas.height / 2 - 20)
      
      ctx.font = '16px sans-serif'
      ctx.fillStyle = theme.textMuted
      ctx.fillText('WebGL/Three.js rendering will be implemented here', canvas.width / 2, canvas.height / 2 + 20)
    }

    drawScene()

    // Handle resize
    const handleResize = () => {
      canvas.width = canvas.offsetWidth
      canvas.height = canvas.offsetHeight
      drawScene()
    }

    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [theme.textMuted, theme.textPrimary, theme.worldCanvas, theme.worldGrid])

  return (
    <div className="h-full flex flex-col" style={{ backgroundColor: theme.worldCanvas }}>
      {/* Controls Bar */}
      <div className="border-b p-3" style={{ backgroundColor: theme.panelMuted, borderColor: theme.border }}>
        <div className="flex items-center justify-between">
          <div className="flex space-x-2">
            <button className="px-3 py-1 rounded text-sm" style={{ backgroundColor: theme.panelElevated, color: theme.textPrimary }}>
              Walk
            </button>
            <button className="px-3 py-1 rounded text-sm" style={{ backgroundColor: theme.panelElevated, color: theme.textPrimary }}>
              Fly
            </button>
            <button className="px-3 py-1 rounded text-sm" style={{ backgroundColor: theme.accentStrong, color: theme.accentContrast }}>
              Build
            </button>
          </div>
          <div className="text-sm" style={{ color: theme.textPrimary }}>
            Region: Welcome Area
          </div>
        </div>
      </div>

      {/* 3D Canvas */}
      <div className="flex-1 relative">
        <canvas
          ref={canvasRef}
          className="w-full h-full canvas-container"
        />
        
        {/* Overlay Controls */}
        <div className="absolute bottom-4 right-4 flex flex-col space-y-2">
          <button className="w-12 h-12 rounded-full flex items-center justify-center backdrop-blur-sm" style={{ backgroundColor: theme.worldOverlay, color: theme.textPrimary, border: `1px solid ${theme.border}` }}>
            <span className="text-xl">↑</span>
          </button>
          <div className="flex space-x-2">
            <button className="w-12 h-12 rounded-full flex items-center justify-center backdrop-blur-sm" style={{ backgroundColor: theme.worldOverlay, color: theme.textPrimary, border: `1px solid ${theme.border}` }}>
              <span className="text-xl">←</span>
            </button>
            <button className="w-12 h-12 rounded-full flex items-center justify-center backdrop-blur-sm" style={{ backgroundColor: theme.worldOverlay, color: theme.textPrimary, border: `1px solid ${theme.border}` }}>
              <span className="text-xl">→</span>
            </button>
          </div>
          <button className="w-12 h-12 rounded-full flex items-center justify-center backdrop-blur-sm" style={{ backgroundColor: theme.worldOverlay, color: theme.textPrimary, border: `1px solid ${theme.border}` }}>
            <span className="text-xl">↓</span>
          </button>
        </div>

        {/* Mini Map */}
        <div className="absolute top-4 right-4 w-32 h-32 rounded-lg border backdrop-blur-sm" style={{ backgroundColor: theme.worldOverlay, borderColor: theme.border }}>
          <div className="w-full h-full flex items-center justify-center text-xs" style={{ color: theme.textPrimary }}>
            Mini Map
          </div>
        </div>
      </div>
    </div>
  )
}
