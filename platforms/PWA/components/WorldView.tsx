'use client'

import { useEffect, useRef } from 'react'

export default function WorldView() {
  const canvasRef = useRef<HTMLCanvasElement>(null)

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
      ctx.fillStyle = '#1a1a2e'
      ctx.fillRect(0, 0, canvas.width, canvas.height)

      // Draw grid
      ctx.strokeStyle = '#2a2a4e'
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
      ctx.fillStyle = '#ffffff'
      ctx.font = '24px sans-serif'
      ctx.textAlign = 'center'
      ctx.fillText('3D World View', canvas.width / 2, canvas.height / 2 - 20)
      
      ctx.font = '16px sans-serif'
      ctx.fillStyle = '#888888'
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
  }, [])

  return (
    <div className="h-full flex flex-col bg-gray-900">
      {/* Controls Bar */}
      <div className="bg-gray-800 border-b border-gray-700 p-3">
        <div className="flex items-center justify-between">
          <div className="flex space-x-2">
            <button className="px-3 py-1 bg-gray-700 hover:bg-gray-600 text-white rounded text-sm">
              Walk
            </button>
            <button className="px-3 py-1 bg-gray-700 hover:bg-gray-600 text-white rounded text-sm">
              Fly
            </button>
            <button className="px-3 py-1 bg-gray-700 hover:bg-gray-600 text-white rounded text-sm">
              Build
            </button>
          </div>
          <div className="text-white text-sm">
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
          <button className="w-12 h-12 bg-white bg-opacity-20 hover:bg-opacity-30 rounded-full flex items-center justify-center text-white backdrop-blur-sm">
            <span className="text-xl">↑</span>
          </button>
          <div className="flex space-x-2">
            <button className="w-12 h-12 bg-white bg-opacity-20 hover:bg-opacity-30 rounded-full flex items-center justify-center text-white backdrop-blur-sm">
              <span className="text-xl">←</span>
            </button>
            <button className="w-12 h-12 bg-white bg-opacity-20 hover:bg-opacity-30 rounded-full flex items-center justify-center text-white backdrop-blur-sm">
              <span className="text-xl">→</span>
            </button>
          </div>
          <button className="w-12 h-12 bg-white bg-opacity-20 hover:bg-opacity-30 rounded-full flex items-center justify-center text-white backdrop-blur-sm">
            <span className="text-xl">↓</span>
          </button>
        </div>

        {/* Mini Map */}
        <div className="absolute top-4 right-4 w-32 h-32 bg-black bg-opacity-50 rounded-lg border border-gray-600 backdrop-blur-sm">
          <div className="w-full h-full flex items-center justify-center text-white text-xs">
            Mini Map
          </div>
        </div>
      </div>
    </div>
  )
}