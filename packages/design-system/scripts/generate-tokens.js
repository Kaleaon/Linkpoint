import { execSync } from "child_process";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const projectRoot = path.resolve(__dirname, "../../..");

export function generateTokens() {
  console.warn(
    "[DEPRECATED] packages/design-system/scripts/generate-tokens.js is deprecated. " +
      "Delegating to Python CLI at tools/token_gen/cli.py."
  );
  execSync("python3 tools/token_gen/cli.py generate", {
    cwd: projectRoot,
    stdio: "inherit",
  });
}

// Execute when run directly
if (process.argv[1] && (process.argv[1].endsWith("generate-tokens.js") || process.argv[1].endsWith("generate-tokens"))) {
  generateTokens();
}
