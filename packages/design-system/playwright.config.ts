import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  timeout: 30000,
  expect: {
    timeout: 5000,
  },
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'list',
  use: {
    headless: true,
    actionTimeout: 0,
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: 'mobile-375',
      use: {
        viewport: { width: 375, height: 667 },
      },
    },
    {
      name: 'tablet-768',
      use: {
        viewport: { width: 768, height: 1024 },
      },
    },
    {
      name: 'desktop-1280',
      use: {
        viewport: { width: 1280, height: 800 },
      },
    },
  ],
});
