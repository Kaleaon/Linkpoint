import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    name: "@linkpoint/viewer-client",
    environment: "node",
    include: ["src/**/*.test.ts"],
  },
});
