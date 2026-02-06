/**
 * Second Life and Firestorm Extensions Tests
 */

import { LLSDMap, LLSDArray, LLSDUtils } from '../src/types';
import { SecondLifeLLSDUtils, SLValidationRules } from '../src/secondlife/SecondLifeLLSDUtils';
import { FirestormLLSDUtils, FSValidationRules } from '../src/firestorm/FirestormLLSDUtils';

describe('SecondLife LLSD Utils', () => {
    describe('SL Response Creation', () => {
        test('should create successful response', () => {
            const response = SecondLifeLLSDUtils.createSLResponse(true, 'Success', { result: 42 });
            
            expect(response.success).toBe(true);
            expect(response.message).toBe('Success');
            expect(response.data).toEqual({ result: 42 });
        });

        test('should create error response without data', () => {
            const response = SecondLifeLLSDUtils.createSLResponse(false, 'Error occurred');
            
            expect(response.success).toBe(false);
            expect(response.message).toBe('Error occurred');
            expect(response.data).toBeUndefined();
        });
    });

    describe('UUID Validation', () => {
        test('should validate correct SL UUIDs', () => {
            const validUUID = '550e8400-e29b-41d4-a716-446655440000';
            expect(SecondLifeLLSDUtils.isValidSLUUID(validUUID)).toBe(true);
        });

        test('should reject null UUID', () => {
            const nullUUID = '00000000-0000-0000-0000-000000000000';
            expect(SecondLifeLLSDUtils.isValidSLUUID(nullUUID)).toBe(false);
        });

        test('should reject invalid UUIDs', () => {
            expect(SecondLifeLLSDUtils.isValidSLUUID('')).toBe(false);
            expect(SecondLifeLLSDUtils.isValidSLUUID('invalid')).toBe(false);
            expect(SecondLifeLLSDUtils.isValidSLUUID(null as any)).toBe(false);
        });
    });

    describe('Agent Appearance', () => {
        test('should create agent appearance data', () => {
            const agentId = '550e8400-e29b-41d4-a716-446655440000';
            const serialNumber = 123;
            const isTrial = false;
            const attachments: LLSDArray = [{ id: 'attachment1' }];
            
            const appearance = SecondLifeLLSDUtils.createAgentAppearance(
                agentId, serialNumber, isTrial, attachments
            );
            
            expect(appearance.agent_id).toBe(agentId);
            expect(appearance.serial_number).toBe(serialNumber);
            expect(appearance.is_trial_account).toBe(isTrial);
            expect(appearance.attachments).toBe(attachments);
            expect(appearance.appearance_version).toBe(1);
            expect(appearance.cof_version).toBe(1);
        });
    });

    describe('Object Properties', () => {
        test('should create object properties data', () => {
            const objectId = '550e8400-e29b-41d4-a716-446655440000';
            const ownerId = '550e8400-e29b-41d4-a716-446655440001';
            const groupId = '550e8400-e29b-41d4-a716-446655440002';
            const permissions: LLSDMap = { base_mask: 0xFFFFFFFF };
            
            const props = SecondLifeLLSDUtils.createObjectProperties(
                objectId, ownerId, groupId, 'Test Object', 'Description', permissions
            );
            
            expect(props.object_id).toBe(objectId);
            expect(props.owner_id).toBe(ownerId);
            expect(props.group_id).toBe(groupId);
            expect(props.name).toBe('Test Object');
            expect(props.description).toBe('Description');
            expect(props.permissions).toBe(permissions);
            expect(props.sale_info).toBeDefined();
            expect(props.creation_date).toBeDefined();
        });
    });

    describe('Asset Upload Request', () => {
        test('should create asset upload request', () => {
            const assetData = new Uint8Array([1, 2, 3, 4]);
            const request = SecondLifeLLSDUtils.createAssetUploadRequest(
                'texture', 'Test Texture', 'Test Description', assetData, 10
            );
            
            expect(request.asset_type).toBe('texture');
            expect(request.name).toBe('Test Texture');
            expect(request.description).toBe('Test Description');
            expect(request.expected_upload_cost).toBe(10);
            expect(request.inventory_type).toBe(0); // texture inventory type
            expect(request.folder_id).toBeDefined();
            expect(LLSDUtils.isUUIDString(request.folder_id as string)).toBe(true);
        });
    });

    describe('Chat Messages', () => {
        test('should create chat message data', () => {
            const message = SecondLifeLLSDUtils.createChatMessage(
                'TestUser', 1, 0, 'Hello World', [10, 20, 30]
            );
            
            expect(message.from_name).toBe('TestUser');
            expect(message.source_type).toBe(1);
            expect(message.chat_type).toBe(0);
            expect(message.message).toBe('Hello World');
            expect(message.position).toEqual([10, 20, 30]);
            expect(message.audible).toBe(1.0);
            expect(message.timestamp).toBeDefined();
        });
    });

    describe('Sim Stats', () => {
        test('should create sim stats data', () => {
            const regionId = '550e8400-e29b-41d4-a716-446655440000';
            const stats = SecondLifeLLSDUtils.createSimStats(
                regionId, 1.0, 45.0, 44.9, 50, 10, 5, 1000, 800, 50
            );
            
            expect(stats.region_id).toBe(regionId);
            expect(stats.time_dilation).toBe(1.0);
            expect(stats.sim_fps).toBe(45.0);
            expect(stats.physics_fps).toBe(44.9);
            expect(stats.root_agents).toBe(10);
            expect(stats.child_agents).toBe(5);
            expect(stats.timestamp).toBeDefined();
        });
    });

    describe('Validation', () => {
        test('should validate map structures', () => {
            const rules = new SLValidationRules()
                .requireMap()
                .requireField('name', 'string')
                .requireField('age', 'number');
            
            const validData = { name: 'Alice', age: 30, extra: 'value' };
            const result = SecondLifeLLSDUtils.validateSLStructure(validData, rules);
            
            expect(result.isValid()).toBe(true);
            expect(result.getErrors()).toHaveLength(0);
        });

        test('should report missing required fields', () => {
            const rules = new SLValidationRules()
                .requireMap()
                .requireField('name')
                .requireField('age');
            
            const invalidData = { name: 'Alice' }; // missing age
            const result = SecondLifeLLSDUtils.validateSLStructure(invalidData, rules);
            
            expect(result.isValid()).toBe(false);
            expect(result.getErrors()).toContain('Missing required field: age');
        });

        test('should warn about type mismatches', () => {
            const rules = new SLValidationRules()
                .requireMap()
                .requireField('age', 'number');
            
            const invalidData = { age: 'thirty' }; // wrong type
            const result = SecondLifeLLSDUtils.validateSLStructure(invalidData, rules);
            
            expect(result.isValid()).toBe(true); // warnings don't make it invalid
            expect(result.getWarnings()).toContain('Field age expected number but got string');
        });
    });
});

describe('Firestorm LLSD Utils', () => {
    describe('RLV Commands', () => {
        test('should create RLV command', () => {
            const sourceId = '550e8400-e29b-41d4-a716-446655440000';
            const command = new FirestormLLSDUtils.RLVCommand('@sit', 'ground', '=force', sourceId);
            
            const llsdData = command.toLLSD();
            expect(llsdData.behaviour).toBe('@sit');
            expect(llsdData.option).toBe('ground');
            expect(llsdData.param).toBe('=force');
            expect(llsdData.source_id).toBe(sourceId);
            expect(llsdData.timestamp).toBeDefined();
        });

        test('should convert command to string', () => {
            const command = new FirestormLLSDUtils.RLVCommand('@sit', 'ground', '=force', 'test-id');
            expect(command.toString()).toBe('@sit:ground=force');
        });
    });

    describe('Radar Data', () => {
        test('should create radar data', () => {
            const radarData = FirestormLLSDUtils.createRadarData(
                '550e8400-e29b-41d4-a716-446655440000',
                'Test User',
                'testuser.resident',
                [128, 128, 25],
                15.5,
                false,
                [{ attachment: 'test' }]
            );
            
            expect(radarData.agent_id).toBe('550e8400-e29b-41d4-a716-446655440000');
            expect(radarData.display_name).toBe('Test User');
            expect(radarData.user_name).toBe('testuser.resident');
            expect(radarData.distance).toBe(15.5);
            expect(radarData.is_typing).toBe(false);
            expect(radarData.radar_version).toBe('6.0.0');
        });
    });

    describe('Bridge Communication', () => {
        test('should create bridge message', () => {
            const requestId = LLSDUtils.generateUUID();
            const parameters = { target: 'avatar', action: 'get_data' };
            
            const message = FirestormLLSDUtils.createBridgeMessage(
                'get_avatar_data', parameters, requestId, 2
            );
            
            expect(message.command).toBe('get_avatar_data');
            expect(message.parameters).toBe(parameters);
            expect(message.request_id).toBe(requestId);
            expect(message.priority).toBe(2);
            expect(message.bridge_version).toBe('6.0.0');
        });
    });

    describe('Performance Stats', () => {
        test('should create performance statistics', () => {
            const stats = FirestormLLSDUtils.createPerformanceStats(
                60.0, 500, 1024, 16.67, 5.2, 150000
            );
            
            expect(stats.fps).toBe(60.0);
            expect(stats.bandwidth).toBe(500);
            expect(stats.memory_usage).toBe(1024);
            expect(stats.render_time).toBe(16.67);
            expect(stats.script_time).toBe(5.2);
            expect(stats.triangles).toBe(150000);
            expect(stats.firestorm_version).toBe('6.0.0');
        });
    });

    describe('Enhanced Particle System', () => {
        test('should create enhanced particle system data', () => {
            const particleSystem = FirestormLLSDUtils.createEnhancedParticleSystem(
                '550e8400-e29b-41d4-a716-446655440000', // sourceId
                '550e8400-e29b-41d4-a716-446655440001', // ownerKey
                1, 10.0, 0.0, 0.1, 0.2, // pattern, ages, angles
                1.0, 10, 1.0, 2.0, 5.0, // burst params
                0.0, 0.0, -9.8, // acceleration
                '550e8400-e29b-41d4-a716-446655440002', // texture
                '550e8400-e29b-41d4-a716-446655440003', // target
                0x01,  // flags
                1.0, 1.0, 1.0, 1.0, // start color
                0.0, 0.0, 0.0, 0.0, // end color
                1.0, 1.0, 0.5, 0.5  // scales
            );
            
            expect(particleSystem.source_id).toBe('550e8400-e29b-41d4-a716-446655440000');
            expect(particleSystem.owner_key).toBe('550e8400-e29b-41d4-a716-446655440001');
            expect(particleSystem.pattern).toBe(1);
            expect(particleSystem.max_age).toBe(10.0);
            expect(particleSystem.accel).toEqual([0.0, 0.0, -9.8]);
            expect(particleSystem.start_color).toEqual([1.0, 1.0, 1.0, 1.0]);
            expect(particleSystem.firestorm_enhanced).toBe(true);
        });
    });

    describe('Firestorm Validation', () => {
        test('should validate Firestorm structures', () => {
            const rules = new FSValidationRules()
                .requireMap()
                .requireFSVersion('6.0.0')
                .requireRLV()
                .requireField('command', 'string');
            
            const validData = {
                command: 'test',
                firestorm_version: '6.0.0',
                rlv_enabled: true
            };
            
            const result = FirestormLLSDUtils.validateFSStructure(validData, rules);
            expect(result.isValid()).toBe(true);
        });

        test('should report missing Firestorm version', () => {
            const rules = new FSValidationRules()
                .requireFSVersion('6.0.0');
            
            const invalidData = { command: 'test' }; // missing version
            const result = FirestormLLSDUtils.validateFSStructure(invalidData, rules);
            
            expect(result.isValid()).toBe(false);
            expect(result.getErrors().some((e: string) => e.includes('Firestorm version'))).toBe(true);
        });

        test('should warn about missing RLV', () => {
            const rules = new FSValidationRules()
                .requireRLV();
            
            const invalidData = { command: 'test' }; // missing RLV
            const result = FirestormLLSDUtils.validateFSStructure(invalidData, rules);
            
            expect(result.getWarnings().some((w: string) => w.includes('RLV'))).toBe(true);
        });
    });

    describe('Cache System', () => {
        test('should store and retrieve cached data', () => {
            const cache = new FirestormLLSDUtils.FSLLSDCache(10000); // 10 second TTL
            const testData = { test: 'data', number: 42 };
            
            cache.put('test-key', testData);
            const retrieved = cache.get('test-key');
            
            expect(retrieved).toEqual(testData);
            expect(retrieved).not.toBe(testData); // Should be deep copied
        });

        test('should return null for expired data', (done) => {
            const cache = new FirestormLLSDUtils.FSLLSDCache(1); // 1ms TTL
            const testData = { test: 'data' };
            
            cache.put('test-key', testData);
            
            setTimeout(() => {
                const retrieved = cache.get('test-key');
                expect(retrieved).toBe(null);
                done();
            }, 5);
        });

        test('should return null for non-existent keys', () => {
            const cache = new FirestormLLSDUtils.FSLLSDCache();
            expect(cache.get('non-existent')).toBe(null);
        });

        test('should clear all cached data', () => {
            const cache = new FirestormLLSDUtils.FSLLSDCache();
            cache.put('key1', 'data1');
            cache.put('key2', 'data2');
            
            expect(cache.size()).toBe(2);
            cache.clear();
            expect(cache.size()).toBe(0);
            expect(cache.get('key1')).toBe(null);
        });
    });

    describe('Version Compatibility', () => {
        test('should validate compatible versions', () => {
            const rules = new FSValidationRules()
                .requireFSVersion('6.0.0');
            
            const compatibleData = { firestorm_version: '6.5.0' };
            const result = FirestormLLSDUtils.validateFSStructure(compatibleData, rules);
            
            expect(result.isValid()).toBe(true);
        });

        test('should reject incompatible versions', () => {
            const rules = new FSValidationRules()
                .requireFSVersion('6.0.0');
            
            const incompatibleData = { firestorm_version: '5.9.0' };
            const result = FirestormLLSDUtils.validateFSStructure(incompatibleData, rules);
            
            expect(result.isValid()).toBe(false);
            expect(result.getErrors().some((e: string) => e.includes('Incompatible'))).toBe(true);
        });
    });

    describe('Deep Copy', () => {
        test('should provide deep copy functionality', () => {
            const original = { nested: { value: 42 }, array: [1, 2, 3] };
            const copy = FirestormLLSDUtils.deepCopy(original);
            
            expect(copy).toEqual(original);
            expect(copy).not.toBe(original);
            expect((copy as any).nested).not.toBe((original as any).nested);
        });
    });
});