import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
import path from "path";

export default defineConfig({
  plugins: [react()],
  test: {
    environment: "jsdom",
    globals: true,
    silent: true,
    setupFiles: [path.resolve(__dirname, "./apps/linkpoint/vitest.setup.ts")],
    include: [
      "packages/**/*.test.ts",
      "apps/linkpoint/src/lib/llsd.test.ts",
      "apps/linkpoint/src/linkpoint/__tests__/**/*.{test,spec}.{ts,tsx,jsx}",
      "apps/linkpoint/src/components/__tests__/**/*.{test,spec}.{ts,tsx,jsx}"
    ],
    coverage: {
      provider: "v8",
      reporter: ["text", "lcov", "json-summary", "json"],
      reportsDirectory: "./coverage",
      exclude: [
        "node_modules/**",
        "dist/**",
        "**/*.d.ts",
        "**/*.test.ts",
        "**/*.test.tsx",
        "**/__tests__/**"
      ]
    }
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./apps/linkpoint"),
    },
  },
});
