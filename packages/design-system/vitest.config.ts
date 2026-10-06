import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    name: "@linkpoint/design-system",
    environment: "node",
    include: ["src/__tests__/**/*.test.ts"],
  },
});
