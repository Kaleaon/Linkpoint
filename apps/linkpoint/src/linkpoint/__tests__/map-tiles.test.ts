import { describe, expect, it } from 'vitest';
import { indexBlocks, mapRange, mapTileUrl, mapTiles, ratingName, teleportTarget } from '../map-tiles';

describe('map layout', () => {
  it('puts north at the top and the current region in the middle', () => {
    const tiles = mapTiles({ x: 1000, y: 1000 }, 1);
    expect(tiles).toHaveLength(9);
    expect(tiles[0]).toMatchObject({ x: 999, y: 1001, column: 0, row: 0, center: false });
    expect(tiles[4]).toMatchObject({ x: 1000, y: 1000, center: true });
    expect(tiles[8]).toMatchObject({ x: 1001, y: 999 });
  });

  it('drops squares off the edge of the grid and clamps the request range', () => {
    expect(mapTiles({ x: 0, y: 0 }, 1).every((tile) => tile.x >= 0 && tile.y >= 0)).toBe(true);
    expect(mapRange({ x: 1, y: 1000 }, 3)).toEqual({ minX: 0, maxX: 4, minY: 997, maxY: 1003 });
  });

  it('names ratings, tile urls and teleport targets', () => {
    expect([13, 21, 42, 0].map(ratingName)).toEqual(['General', 'Moderate', 'Adult', 'Unknown']);
    expect(mapTileUrl(1000, 1001)).toBe('https://map.secondlife.com/map-1-1000-1001-objects.jpg');
    expect(teleportTarget({ name: 'Ahern' })).toBe('Ahern/128/128/30');
  });

  it('leaves squares without a region absent so they draw as open water', () => {
    const index = indexBlocks([{ x: 5, y: 6, name: 'Ahern', access: 13 }]);
    expect(index.get('5,6')?.name).toBe('Ahern');
    expect(index.get('6,6')).toBeUndefined();
  });
});
