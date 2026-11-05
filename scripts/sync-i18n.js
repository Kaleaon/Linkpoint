#!/usr/bin/env node

/**
 * sync-i18n.js
 *
 * Synchronizes translation keys between Web JSON resources (apps/linkpoint/src/locales/{lang}/translation.json)
 * and Android XML resources (Linkpoint/src/main/res/values[-{lang}]/strings.xml).
 *
 * Usage:
 *   node scripts/sync-i18n.js          # Syncs Web JSON keys to Android XML files
 *   node scripts/sync-i18n.js --check  # Verifies key parity across platforms and languages (returns non-zero on mismatch)
 */

const fs = require('fs');
const path = require('path');

const rootDir = path.resolve(__dirname, '..');
const webLocalesDir = path.join(rootDir, 'apps/linkpoint/src/locales');

// Determine Android res directory
let androidResDir = path.join(rootDir, 'legacy/Linkpoint/src/main/res');
if (!fs.existsSync(androidResDir)) {
  androidResDir = path.join(rootDir, 'Linkpoint/src/main/res');
}
if (!fs.existsSync(androidResDir)) {
  androidResDir = path.join(rootDir, 'app/src/main/res');
}

const SUPPORTED_LOCALES = ['en', 'es', 'fr', 'ja'];
const isCheckMode = process.argv.includes('--check') || process.argv.includes('--verify');

function normalizeKey(key) {
  return key.toLowerCase().replace(/[^a-z0-9_]/g, '_');
}

function escapeXmlValue(value) {
  if (typeof value !== 'string') return value;
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/'/g, "\\'")
    .replace(/"/g, '\\"');
}

function parseAndroidXmlKeys(xmlContent) {
  const keys = new Map();
  const stringRegex = /<string\s+name=["']([^"']+)["']>([\s\S]*?)<\/string>/g;
  let match;
  while ((match = stringRegex.exec(xmlContent)) !== null) {
    keys.set(match[1], match[2]);
  }
  return keys;
}

function getAndroidPathForLocale(lang) {
  if (lang === 'en') {
    return path.join(androidResDir, 'values', 'strings.xml');
  }
  return path.join(androidResDir, `values-${lang}`, 'strings.xml');
}

function syncLocale(lang) {
  const jsonPath = path.join(webLocalesDir, lang, 'translation.json');
  if (!fs.existsSync(jsonPath)) {
    console.error(`[sync-i18n] Error: Web locale file not found: ${jsonPath}`);
    process.exit(1);
  }

  const rawJson = fs.readFileSync(jsonPath, 'utf8');
  const webTranslations = JSON.parse(rawJson);
  const targetXmlPath = getAndroidPathForLocale(lang);

  const xmlDir = path.dirname(targetXmlPath);
  if (!fs.existsSync(xmlDir)) {
    fs.mkdirSync(xmlDir, { recursive: true });
  }

  let existingKeys = new Map();
  if (fs.existsSync(targetXmlPath)) {
    const existingXml = fs.readFileSync(targetXmlPath, 'utf8');
    existingKeys = parseAndroidXmlKeys(existingXml);
  }

  // Load English baseline keys (from values/strings.xml) for fallback
  let enKeys = new Map();
  const enXmlPath = getAndroidPathForLocale('en');
  if (lang !== 'en' && fs.existsSync(enXmlPath)) {
    enKeys = parseAndroidXmlKeys(fs.readFileSync(enXmlPath, 'utf8'));
  }

  let xmlContent = '<?xml version="1.0" encoding="utf-8"?>\n<resources>\n';
  // Merge English baseline keys, existing locale keys, and Web keys
  const mergedMap = new Map(lang === 'en' ? existingKeys : enKeys);

  // Overlay existing locale keys if lang != 'en'
  for (const [key, value] of existingKeys.entries()) {
    mergedMap.set(key, value);
  }

  // Overlay Web keys
  for (const [key, value] of Object.entries(webTranslations)) {
    const normKey = normalizeKey(key);
    mergedMap.set(normKey, escapeXmlValue(value));
  }

  for (const [key, val] of mergedMap.entries()) {
    xmlContent += `    <string name="${key}">${val}</string>\n`;
  }
  xmlContent += '</resources>\n';

  fs.writeFileSync(targetXmlPath, xmlContent, 'utf8');
  console.log(`[sync-i18n] Synced ${mergedMap.size} strings for locale '${lang}' -> ${targetXmlPath}`);
}

function checkParity() {
  console.log('[sync-i18n] Checking translation key parity across Web and Android...');
  let hasErrors = false;

  // Load English Web keys as baseline
  const enJsonPath = path.join(webLocalesDir, 'en', 'translation.json');
  const enWebTranslations = JSON.parse(fs.readFileSync(enJsonPath, 'utf8'));
  const baselineWebKeys = Object.keys(enWebTranslations).map(normalizeKey);

  for (const lang of SUPPORTED_LOCALES) {
    const jsonPath = path.join(webLocalesDir, lang, 'translation.json');
    if (!fs.existsSync(jsonPath)) {
      console.error(`[sync-i18n] FAIL: Missing Web locale file for '${lang}': ${jsonPath}`);
      hasErrors = true;
      continue;
    }

    const webObj = JSON.parse(fs.readFileSync(jsonPath, 'utf8'));
    const webKeys = new Set(Object.keys(webObj).map(normalizeKey));

    // Check Web locale parity against English baseline
    const missingInWeb = baselineWebKeys.filter(k => !webKeys.has(k));
    if (missingInWeb.length > 0) {
      console.error(`[sync-i18n] FAIL: Web locale '${lang}' is missing keys:`, missingInWeb);
      hasErrors = true;
    }

    // Check Android XML parity
    const xmlPath = getAndroidPathForLocale(lang);
    if (!fs.existsSync(xmlPath)) {
      console.error(`[sync-i18n] FAIL: Missing Android resource file for '${lang}': ${xmlPath}`);
      hasErrors = true;
      continue;
    }

    const xmlContent = fs.readFileSync(xmlPath, 'utf8');
    const androidKeys = parseAndroidXmlKeys(xmlContent);

    const missingInAndroid = baselineWebKeys.filter(k => !androidKeys.has(k));
    if (missingInAndroid.length > 0) {
      console.error(`[sync-i18n] FAIL: Android locale '${lang}' is missing keys:`, missingInAndroid);
      hasErrors = true;
    }
  }

  if (hasErrors) {
    console.error('[sync-i18n] Translation key parity check FAILED.');
    process.exit(1);
  } else {
    console.log('[sync-i18n] SUCCESS: All translation keys are in parity across Web and Android!');
  }
}

function main() {
  // First sync to ensure files exist
  for (const lang of SUPPORTED_LOCALES) {
    syncLocale(lang);
  }

  if (isCheckMode) {
    checkParity();
  }
}

main();
