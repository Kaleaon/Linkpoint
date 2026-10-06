import { test, expect } from '@playwright/test';
import { computeThemeTokens } from '../src/tokens/index.js';

test.describe('Multi-Viewport Web Layout Matrix', () => {
  test('renders design system tokens and layout structures', async ({ page }) => {
    const viewport = page.viewportSize();
    expect(viewport).not.toBeNull();

    const themeTokens = computeThemeTokens('terminal', 'ink');

    const htmlContent = `
      <!DOCTYPE html>
      <html lang="en">
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Design System Layout Spec</title>
        <style>
          :root {
            --bg: ${themeTokens.bg};
            --surf: ${themeTokens.surf};
            --surf2: ${themeTokens.surf2};
            --ink: ${themeTokens.ink};
            --ink2: ${themeTokens.ink2};
            --pri: ${themeTokens.pri};
            --sec: ${themeTokens.sec};
            --rs: ${themeTokens.rs};
            --rl: ${themeTokens.rl};
            --rp: ${themeTokens.rp};
            --pad: ${themeTokens.pad};
            font-family: ${themeTokens.font};
          }
          * { box-sizing: border-box; margin: 0; padding: 0; }
          body {
            background-color: var(--bg);
            color: var(--ink);
            padding: var(--pad);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            gap: var(--pad);
          }
          header {
            background: var(--surf);
            border: 1px solid var(--surf2);
            border-radius: var(--rl);
            padding: 16px;
            display: flex;
            justify-content: space-between;
            align-items: center;
          }
          .title {
            font-size: 1.25rem;
            font-weight: bold;
            color: var(--pri);
          }
          .viewport-badge {
            background: var(--surf2);
            padding: 4px 8px;
            border-radius: var(--rs);
            font-size: 0.85rem;
            color: var(--sec);
          }
          .layout-grid {
            display: grid;
            grid-template-columns: 1fr;
            gap: 12px;
          }
          @media (min-width: 768px) {
            .layout-grid {
              grid-template-columns: 240px 1fr;
            }
          }
          @media (min-width: 1280px) {
            .layout-grid {
              grid-template-columns: 280px 1fr 320px;
            }
          }
          .card {
            background: var(--surf);
            border: 1px solid var(--surf2);
            border-radius: var(--rp);
            padding: 16px;
          }
          .card-title {
            color: var(--pri);
            margin-bottom: 8px;
            font-size: 1rem;
          }
        </style>
      </head>
      <body>
        <header>
          <div class="title">Linkpoint Design System</div>
          <div class="viewport-badge" id="viewport-display">${viewport!.width}px x ${viewport!.height}px</div>
        </header>
        <div class="layout-grid">
          <nav class="card">
            <div class="card-title">Navigation</div>
            <p>Active Layout: Terminal</p>
          </nav>
          <main class="card">
            <div class="card-title">Main Content</div>
            <p>Responsive layout test running on viewport ${viewport!.width}px.</p>
          </main>
          <aside class="card">
            <div class="card-title">Inspector</div>
            <p>Palette: Ink Terminal</p>
          </aside>
        </div>
      </body>
      </html>
    `;

    await page.setContent(htmlContent);

    const badge = page.locator('#viewport-display');
    await expect(badge).toHaveText(`${viewport!.width}px x ${viewport!.height}px`);

    const header = page.locator('header');
    await expect(header).toBeVisible();

    const layoutGrid = page.locator('.layout-grid');
    await expect(layoutGrid).toBeVisible();

    const boundingBox = await layoutGrid.boundingBox();
    expect(boundingBox).not.toBeNull();
    expect(boundingBox!.width).toBeGreaterThan(0);
  });
});
