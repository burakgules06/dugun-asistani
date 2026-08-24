import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    host: true, // listen on the LAN, not just localhost, so phones on the same network can reach the dev server
    allowedHosts: true, // needed so an ngrok tunnel host (random subdomain each run) isn't rejected
    // Backend calls go through this proxy (relative /api paths) instead of an absolute LAN URL, so the
    // app works unmodified whether it's reached via LAN IP or an HTTPS tunnel (ngrok) without mixed-content errors.
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
})
