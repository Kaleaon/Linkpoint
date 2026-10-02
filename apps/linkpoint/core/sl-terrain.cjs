// Region terrain data the renderer needs to texture the land and place the water.

const ZERO_UUID = '00000000-0000-0000-0000-000000000000';
const finite = (value, fallback = 0) => (Number.isFinite(Number(value)) ? Number(value) : fallback);

/**
 * From the RegionHandshake: the four detail texture ids (low to high), the start height and height
 * range at each corner in the handshake's order 00, 01, 10, 11 (south-west, south-east, north-west,
 * north-east), the region's global origin in metres, and the water height.
 */
function serializeTerrainMaterials(region) {
  if (!region) return null;
  const ids = [region.terrainDetail0, region.terrainDetail1, region.terrainDetail2, region.terrainDetail3]
    .map((id) => id?.toString?.() || null)
    .map((id) => (id && id !== ZERO_UUID ? id : null));
  return {
    textureIds: ids,
    startHeights: [region.terrainStartHeight00, region.terrainStartHeight01, region.terrainStartHeight10, region.terrainStartHeight11].map((v) => finite(v, 20)),
    heightRanges: [region.terrainHeightRange00, region.terrainHeightRange01, region.terrainHeightRange10, region.terrainHeightRange11].map((v) => finite(v, 60)),
    origin: [finite(region.xCoordinate), finite(region.yCoordinate)],
    waterHeight: Number.isFinite(Number(region.waterHeight)) ? Number(region.waterHeight) : null,
  };
}

module.exports = { serializeTerrainMaterials };
