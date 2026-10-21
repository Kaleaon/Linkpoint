/**
 * Modern Second Life 3D Viewer Mesh & PBR Asset Decoder and Shader Utilities.
 */

import {
    DetailLevel,
    AssetSchemaConstants,
    TextureTransformAdapter,
    JointInfluence,
    Vector2,
    Vector3,
    MeshBlockAdapter,
    GLTFMaterialAdapter,
    AlphaMode
} from './AssetSchemaAdapter.js';

export class MeshAssetDecoder {
    /**
     * Decode rigged mesh joint influences from binary data.
     * Supports extended skeletons with up to 256 joints (0..255) and 0xFF sentinel byte.
     */
    public static parseJointInfluences(buffer: Uint8Array, offset: number = 0): { jointInfluences: JointInfluence[]; bytesRead: number } {
        const influences: JointInfluence[] = [];
        let cur = offset;

        while (cur < buffer.length) {
            const jointIdx = buffer[cur++];
            if (jointIdx === AssetSchemaConstants.JOINT_SENTINEL) {
                break; // 0xFF end of list sentinel
            }

            if (cur + 2 > buffer.length) break;
            const b0 = buffer[cur++];
            const b1 = buffer[cur++];
            const uint16Weight = (b1 << 8) | b0;
            const weight = uint16Weight / 65535.0;

            influences.push(new JointInfluence(jointIdx, weight));
        }

        return {
            jointInfluences: influences,
            bytesRead: cur - offset
        };
    }

    /**
     * Compute GPU texture transform uniform matrix / array (8-float padded for 16-byte alignment or 5-float tight).
     */
    public static createTextureTransform(
        scaleX: number = 1.0,
        scaleY: number = 1.0,
        rotation: number = 0.0,
        offsetX: number = 0.0,
        offsetY: number = 0.0,
        tight: boolean = false
    ): Float32Array {
        const adapter = new TextureTransformAdapter(scaleX, scaleY, rotation, offsetX, offsetY);
        return tight ? adapter.getPackedTight() : adapter.getPacked();
    }

    /**
     * Select mesh block key based on LOD detail level.
     */
    public static getLodKey(lod: DetailLevel): string {
        return AssetSchemaConstants.getLodKey(lod);
    }

    /**
     * Construct modern GLTF material adapter with alpha mode, cutoff, and texture transforms.
     */
    public static createGLTFMaterial(
        alphaMode: AlphaMode = AlphaMode.OPAQUE,
        alphaCutoff: number = 0.5,
        doubleSided: boolean = false,
        transforms: TextureTransformAdapter[] = []
    ): GLTFMaterialAdapter {
        return new GLTFMaterialAdapter(alphaMode, alphaCutoff, doubleSided, transforms);
    }

    /**
     * Create mesh block adapter structure.
     */
    public static createMeshBlock(
        lod: DetailLevel,
        positions: Vector3[] = [],
        normals: Vector3[] = [],
        texCoords: Vector2[] = [],
        jointInfluences: JointInfluence[] = []
    ): MeshBlockAdapter {
        return new MeshBlockAdapter(lod, positions, normals, texCoords, jointInfluences);
    }
}
