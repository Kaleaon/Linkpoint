import { test, assert } from 'vitest';
import {
  LLSD,
  LLSDValue,
  LLSDSerialize,
  LLSDDate,
  LLUUID,
  Vector2,
  Vector3,
  JointInfluence,
  GLTFMaterialAdapter,
  MeshBlockAdapter,
  TextureTransformAdapter,
  DetailLevel
} from '../index.js';

test('64-bit integer handling', () => {
  const bigInt = 9223372036854775807n;
  const sd = LLSDValue.integer(bigInt);
  assert.equal(sd.asBigInt(), bigInt);
});

test('Binary date LE endianness', () => {
  const sd = LLSDValue.date(new LLSDDate(123456789.0));
  const bin = LLSDSerialize.toBinary(sd);
  const back = LLSDSerialize.fromBinary(bin);
  assert.ok(Math.abs(back.asDate().secondsSinceEpoch - 123456789.0) < 0.001);
});

test('Notation roundtrip', () => {
  const sd = LLSDValue.integer(42n);
  const notation = LLSDSerialize.toNotation(sd);
  assert.equal(notation, 'i42');
  const back = LLSDSerialize.fromNotation(notation);
  assert.equal(back.asBigInt(), 42n);
});

test('XML roundtrip and auto-detect', () => {
  const mapVal = LLSDValue.map({
    bool_true: LLSDValue.boolean(true),
    int_val: LLSDValue.integer(100),
    string_val: LLSDValue.string('hello & world')
  });

  const xmlStr = LLSDSerialize.toXML(mapVal);
  const parsed = LLSDSerialize.parse(xmlStr);
  assert.equal(parsed.get('bool_true').asBoolean(), true);
  assert.equal(parsed.get('int_val').asInteger(), 100);
  assert.equal(parsed.get('string_val').asString(), 'hello & world');
});

test('JSON roundtrip', () => {
  const original = LLSDValue.map({
    key1: LLSDValue.string('value1'),
    key2: LLSDValue.integer(123)
  });
  const jsonStr = LLSDSerialize.toJSON(original);
  const back = LLSDSerialize.fromJSON(jsonStr);
  assert.equal(back.get('key1').asString(), 'value1');
  assert.equal(back.get('key2').asInteger(), 123);
});

test('Executable schema adapter classes and packing utilities', () => {
  const v2 = new Vector2(1.0, 2.0);
  assert.deepEqual(Array.from(v2.getPacked()), [1.0, 2.0]);

  const v3 = new Vector3(1.0, 2.0, 3.0);
  assert.deepEqual(Array.from(v3.getPacked()), [1.0, 2.0, 3.0]);

  const ji = new JointInfluence(5, 0.75);
  assert.deepEqual(Array.from(ji.getPacked()), [5, 0.75]);

  const tt = new TextureTransformAdapter(2.0, 3.0, 1.0, 0.5, 0.25);
  const mat = new GLTFMaterialAdapter(0, 0.5, false, [tt]);
  assert.equal(mat.getPackedTransforms().length, 8);

  const block = new MeshBlockAdapter(DetailLevel.HIGHEST, [v3], [v3], [v2], [ji]);
  assert.deepEqual(Array.from(block.getPackedPositions()), [1.0, 2.0, 3.0]);
  assert.deepEqual(Array.from(block.getPackedNormals()), [1.0, 2.0, 3.0]);
  assert.deepEqual(Array.from(block.getPackedTexCoords()), [1.0, 2.0]);
});
