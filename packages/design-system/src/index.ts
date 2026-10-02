/**
 * @linkpoint/design-system root export.
 */

export * from "./tokens/index.js";
export * from "./css/index.js";
export * from "./react/index.js";

export async function loadTheme(name: import("./tokens/index.js").ThemeName): Promise<unknown> {
  return (await import(`../themes/${name}.json`)).default;
}
