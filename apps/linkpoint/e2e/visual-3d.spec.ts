import { test, expect } from '@playwright/test';

test.describe('3D Graphics Visual Snapshot Regression Suite', () => {
  const fixtures = [
    { id: 'renderer-pbr', name: 'Linkpoint Renderer PBR Material Pipeline' },
    { id: 'renderer-skinning', name: 'Linkpoint Renderer Skeletal Joint Skinning' },
    { id: 'renderer-terrain-sky', name: 'Linkpoint Renderer Terrain and Sky Environment' },
    { id: 'graphics3d-pbr', name: 'Graphics3D Cook-Torrance PBR Shader' },
    { id: 'graphics3d-skinned', name: 'Graphics3D GPU Joint Skinning Shader' },
    { id: 'graphics3d-terrain', name: 'Graphics3D Multi-Texture Terrain Shader' },
    { id: 'graphics3d-sky', name: 'Graphics3D Sky Atmosphere Stars and Water Shaders' },
  ];

  for (const fixture of fixtures) {
    test(`renders canonical fixture: ${fixture.name}`, async ({ page }) => {
      await page.goto(`/visual-harness.html?fixture=${fixture.id}`);
      
      await page.waitForFunction(
        () => window.isRenderComplete === true || window.renderError !== undefined,
        { timeout: 20000 }
      );

      const renderError = await page.evaluate(() => window.renderError);
      expect(renderError, `Render error in ${fixture.id}: ${renderError}`).toBeUndefined();

      const canvasContainer = page.locator('#canvas-container');
      await expect(canvasContainer).toBeVisible();

      await expect(canvasContainer).toHaveScreenshot(`${fixture.id}.png`, {
        maxDiffPixelRatio: 0.01,
      });
    });
  }
});
