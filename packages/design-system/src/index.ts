/**
 * @linkpoint/design-system root export.
 */

export * from "./tokens/index.js";
export * from "./css/index.js";
export * from "./react/index.js";

export async function loadTheme(name: import("./tokens/index.js").ThemeName): Promise<unknown> {
  const themeModules: Record<string, () => Promise<any>> =
    typeof import.meta !== "undefined" && typeof (import.meta as any).glob === "function"
      ? (import.meta as any).glob("../themes/*.json")
      : {};

  const key = `../themes/${name}.json`;
  if (themeModules[key]) {
    const mod = await themeModules[key]();
    return mod.default || mod;
  }
  return (await import(`../themes/${name}.json`)).default;
}
