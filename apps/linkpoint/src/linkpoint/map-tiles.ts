/** Layout and labelling for the world map, following the official viewer's map floater. */

export interface MapBlock {
  x: number; y: number; name: string;
  /** SimAccess: 13 PG, 21 Mature, 42 Adult. */
  access: number;
  waterHeight?: number; regionFlags?: number; mapImage?: string | null;
}

export interface MapTile { x: number; y: number; column: number; row: number; center: boolean }

/** The tiles of a (2r+1) square around a region, north at the top (row 0). */
export function mapTiles(center: { x: number; y: number }, radius: number): MapTile[] {
  const tiles: MapTile[] = [];
  for (let row = 0; row <= radius * 2; row++) {
    for (let column = 0; column <= radius * 2; column++) {
      tiles.push({
        x: center.x + column - radius, y: center.y + radius - row, column, row,
        center: column === radius && row === radius,
      });
    }
  }
  return tiles.filter((tile) => tile.x >= 0 && tile.y >= 0);
}

/** Region-grid range covering the tiles, for one map block request. */
export function mapRange(center: { x: number; y: number }, radius: number) {
  return {
    minX: Math.max(0, center.x - radius), maxX: center.x + radius,
    minY: Math.max(0, center.y - radius), maxY: center.y + radius,
  };
}

export function ratingName(access: number | undefined): string {
  if (access === 13) return 'General';
  if (access === 21) return 'Moderate';
  if (access === 42) return 'Adult';
  return 'Unknown';
}

/** The tile server's image for a region of Second Life; other grids advertise no tiles. */
export function mapTileUrl(x: number, y: number) {
  return `https://map.secondlife.com/map-1-${x}-${y}-objects.jpg`;
}

/** The colour the official map shows where there is no region: open water. */
export const MAP_WATER_COLOR = '#1b3a5c';

/** Index blocks by region so the grid can look a tile up; a tile without a block is empty ocean. */
export function indexBlocks(blocks: MapBlock[]): Map<string, MapBlock> {
  return new Map(blocks.map((block) => [`${block.x},${block.y}`, block]));
}

/** A teleport destination for the middle of a region. */
export function teleportTarget(block: Pick<MapBlock, 'name'>) {
  return `${block.name}/128/128/30`;
}
