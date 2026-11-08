package com.linkpoint.render.lumiya.spatial

/**
 * Tri-state result of a frustum / AABB classification, matching the
 * LL viewer convention used by `LLCamera::AABBInFrustum` and the LL
 * volume octree walker.
 *
 *  - [OUTSIDE]    – the volume is entirely outside; skip it
 *  - [INTERSECTS] – the volume straddles a plane; recurse / re-test
 *                   children
 *  - [INSIDE]     – the volume is entirely inside; descendants can be
 *                   drawn without further plane tests
 *
 * Producers (octree walks, BVH walks) convert this into a per-node
 * decision. Consumers that only care about visibility can keep using
 * the existing binary `isAABBVisible` helper.
 */
enum class FrustumResult { OUTSIDE, INTERSECTS, INSIDE }
