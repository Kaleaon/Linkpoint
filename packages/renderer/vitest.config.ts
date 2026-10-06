import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    name: "@linkpoint/renderer",
    environment: "node",
    include: ["src/**/*.test.ts"],
  },
});
