import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    name: "@linkpoint/viewer-store",
    environment: "node",
    include: ["src/**/*.test.ts"],
  },
});
