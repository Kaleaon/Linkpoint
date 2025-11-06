const withPWA = require('next-pwa')({
  dest: 'public',
  register: true,
  skipWaiting: true,
  disable: process.env.NODE_ENV === 'development'
})

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: ['secondlife.com', 'lindenlab.com']
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'https://login.agni.lindenlab.com/cgi-bin/:path*'
      }
    ]
  },
  async headers() {
    return [
      {
        source: '/(.*)',
        headers: [
          { key: 'Cross-Origin-Embedder-Policy', value: 'require-corp' },
          { key: 'Cross-Origin-Opener-Policy', value: 'same-origin' }
        ]
      }
    ]
  }
}

module.exports = withPWA(nextConfig)
