/**
 * Linkpoint PWA - Avatar Visual Parameters
 *
 * Standard Second Life / OpenSim visual parameter IDs.
 */

export enum VisualParamId {
  // Body
  HEIGHT = 33,
  THICKNESS = 34,
  BODY_FAT = 35,

  // Skin
  SKIN_COLOR = 1,
  SKIN_GLOSS = 2,

  // Head
  HEAD_SIZE = 12,
  HEAD_STRETCH = 13,
  HEAD_SHAPE = 14,

  // Eyes
  EYE_SIZE = 15,
  EYE_SPACING = 16,
  EYE_COLOR = 17,

  // Mouth
  MOUTH_SIZE = 18,
  MOUTH_WIDTH = 19,
  LIP_THICKNESS = 20,

  // Nose
  NOSE_SIZE = 21,
  NOSE_WIDTH = 22,
  NOSE_BRIDGE = 23,

  // Torso
  TORSO_LENGTH = 24,
  SHOULDER_WIDTH = 25,
  ARM_LENGTH = 26,

  // Legs
  LEG_LENGTH = 27,
  HIP_WIDTH = 28,
  FOOT_SIZE = 29
}

export const VisualParamGroups = [
  {
    name: 'Body',
    params: [
      { id: VisualParamId.HEIGHT, name: 'Height', min: 0, max: 1 },
      { id: VisualParamId.THICKNESS, name: 'Thickness', min: 0, max: 1 },
      { id: VisualParamId.BODY_FAT, name: 'Body Fat', min: 0, max: 1 },
    ]
  },
  {
    name: 'Head & Face',
    params: [
      { id: VisualParamId.HEAD_SIZE, name: 'Head Size', min: 0, max: 1 },
      { id: VisualParamId.EYE_SIZE, name: 'Eye Size', min: 0, max: 1 },
      { id: VisualParamId.MOUTH_SIZE, name: 'Mouth Size', min: 0, max: 1 },
      { id: VisualParamId.NOSE_SIZE, name: 'Nose Size', min: 0, max: 1 },
    ]
  },
  {
    name: 'Limbs',
    params: [
      { id: VisualParamId.ARM_LENGTH, name: 'Arm Length', min: 0, max: 1 },
      { id: VisualParamId.LEG_LENGTH, name: 'Leg Length', min: 0, max: 1 },
      { id: VisualParamId.FOOT_SIZE, name: 'Foot Size', min: 0, max: 1 },
    ]
  }
];
