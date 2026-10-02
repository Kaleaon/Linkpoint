/** A bounded client-side implementation of the common llParticleSystem behaviours. */
export const PARTICLE_FLAGS = { interpColor: 1, interpScale: 2, bounce: 4, wind: 8, followSource: 16, target: 64, emissive: 256 } as const;

export interface ParticleConfig {
  pattern: number; maxAge: number; burstRate: number; burstRadius: number;
  burstSpeedMin: number; burstSpeedMax: number; burstPartCount: number;
  acceleration: number[]; targetId?: string | null; textureId?: string | null; dataFlags: number;
  partMaxAge: number; startColor: number[]; endColor: number[];
  startScale: number[]; endScale: number[];
}

export interface ParticleFrame { id: string; position: number[]; scale: number[]; color: number[]; textureId?: string | null; emissive: boolean }
interface Particle extends ParticleFrame { sourceId: string; born: number; life: number; velocity: number[]; origin: number[]; acceleration: number[]; updated?: number; flags: number; startColor: number[]; endColor: number[]; startScale: number[]; endScale: number[]; targetId?: string | null }
interface Emitter { config: ParticleConfig; started: number; lastBurst: number; serial: number }

export class ParticleEngine {
  private emitters = new Map<string, Emitter>();
  private particles = new Map<string, Particle>();
  constructor(private random: () => number = Math.random, private limit = 512) {}

  setEmitter(id: string, config: ParticleConfig | null, now = 0) {
    if (!config || config.pattern === 0 || config.burstPartCount <= 0) { this.removeEmitter(id); return; }
    const old = this.emitters.get(id);
    this.emitters.set(id, { config, started: old?.started ?? now, lastBurst: old?.lastBurst ?? -Infinity, serial: old?.serial ?? 0 });
  }
  removeEmitter(id: string) { this.emitters.delete(id); for (const [key, p] of this.particles) if (p.sourceId === id) this.particles.delete(key); }
  clear() { this.emitters.clear(); this.particles.clear(); }

  update(now: number, positions: Map<string, number[]>): ParticleFrame[] {
    for (const [sourceId, emitter] of this.emitters) {
      const c = emitter.config;
      if (c.maxAge > 0 && now - emitter.started > c.maxAge) { this.emitters.delete(sourceId); continue; }
      const rate = Math.max(0.01, c.burstRate || 0.1);
      if (now - emitter.lastBurst >= rate && positions.has(sourceId)) {
        emitter.lastBurst = now;
        for (let i = 0; i < Math.min(32, c.burstPartCount); i++) this.spawn(sourceId, emitter, now, positions.get(sourceId)!);
      }
    }
    for (const [id, p] of this.particles) {
      const age = now - p.born;
      if (age >= p.life) { this.particles.delete(id); continue; }
      const dt = Math.min(0.05, Math.max(0, p.updated == null ? 0 : now - p.updated));
      p.updated = now;
      if (p.flags & PARTICLE_FLAGS.target) {
        const target = p.targetId && positions.get(p.targetId);
        if (target) for (let axis = 0; axis < 3; axis++) p.velocity[axis] += (target[axis] - p.position[axis]) * dt * 2;
      }
      for (let axis = 0; axis < 3; axis++) p.velocity[axis] += p.acceleration[axis] * dt;
      if (p.flags & PARTICLE_FLAGS.followSource) {
        const source = positions.get(p.sourceId);
        if (source) for (let axis = 0; axis < 3; axis++) p.origin[axis] = source[axis];
      }
      for (let axis = 0; axis < 3; axis++) p.position[axis] += p.velocity[axis] * dt;
      if ((p.flags & PARTICLE_FLAGS.bounce) && p.position[2] < 0) { p.position[2] = 0; p.velocity[2] = Math.abs(p.velocity[2]); }
      const t = Math.max(0, Math.min(1, age / p.life));
      if (p.flags & PARTICLE_FLAGS.interpColor) p.color = p.startColor.map((v, i) => v + (p.endColor[i] - v) * t);
      if (p.flags & PARTICLE_FLAGS.interpScale) p.scale = p.startScale.map((v, i) => v + (p.endScale[i] - v) * t);
    }
    return [...this.particles.values()];
  }

  private spawn(sourceId: string, emitter: Emitter, now: number, source: number[]) {
    if (this.particles.size >= this.limit) this.particles.delete(this.particles.keys().next().value!);
    const c = emitter.config, azimuth = this.random() * Math.PI * 2;
    const pattern = c.pattern;
    const radial = pattern === 1 ? 0 : pattern === 2 ? Math.asin(this.random() * 2 - 1) : (this.random() - 0.5) * Math.PI;
    const direction = pattern === 1 ? [0, 0, 0] : [Math.cos(azimuth) * Math.cos(radial), Math.sin(azimuth) * Math.cos(radial), Math.sin(radial)];
    const speed = c.burstSpeedMin + (c.burstSpeedMax - c.burstSpeedMin) * this.random();
    const position = source.map((v, i) => v + direction[i] * c.burstRadius);
    const id = `${sourceId}:${emitter.serial++}`;
    this.particles.set(id, { id, sourceId, born: now, life: Math.max(0.05, c.partMaxAge), position, origin: [...source], velocity: direction.map(v => v * speed), scale: [...c.startScale], color: [...c.startColor], textureId: c.textureId, emissive: Boolean(c.dataFlags & PARTICLE_FLAGS.emissive), flags: c.dataFlags, startColor: c.startColor, endColor: c.endColor, startScale: c.startScale, endScale: c.endScale, targetId: c.targetId, acceleration: c.acceleration });
  }
}
