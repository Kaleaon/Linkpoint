export interface SimulatorObjectEvent {
  localID: number;
  object: {
    FullID?: { toString(): string };
    ParentID?: number;
    PCode?: number;
    Position?: { x: number; y: number; z: number };
    Scale?: { x: number; y: number; z: number };
    Rotation?: { x: number; y: number; z: number; w: number };
    name?: string;
  };
}

const finite = (value: number | undefined, fallback = 0) =>
  Number.isFinite(value) ? (value as number) : fallback;

const vector = (
  value: { x: number; y: number; z: number } | undefined,
  fallback: [number, number, number] = [0, 0, 0],
): [number, number, number] =>
  value ? [finite(value.x), finite(value.y), finite(value.z)] : fallback;

/**
 * Converts a protocol object into data that can safely cross a Tauri command
 * or browser structured-clone boundary. Avatar PCode is 47 in the SL protocol.
 */
export function serializeSceneObject(event: SimulatorObjectEvent) {
  const object = event.object;
  const rotation = object.Rotation;
  return {
    id: object.FullID?.toString() || String(event.localID),
    localId: event.localID,
    parentId: object.ParentID || 0,
    pcode: object.PCode,
    avatar: object.PCode === 47,
    position: vector(object.Position),
    scale: vector(object.Scale, [0.5, 0.5, 0.5]),
    rotation: rotation
      ? [finite(rotation.x), finite(rotation.y), finite(rotation.z), finite(rotation.w, 1)]
      : [0, 0, 0, 1],
    name: object.name || "",
  };
}
