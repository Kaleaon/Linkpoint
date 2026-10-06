import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    name: "@linkpoint/viewer-types",
    environment: "node",
    include: ["src/**/*.test.ts"],
  },
});
