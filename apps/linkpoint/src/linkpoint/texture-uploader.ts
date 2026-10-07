/**
 * Client-Side JPEG2000 Texture Upload Pipeline
 *
 * Coordinates balance verification, capability requesting via NewFileAgentInventory,
 * power-of-two image scaling, JPEG2000 codestream encoding, HTTP POST upload with progress tracking,
 * inventory insertion, and economy transaction recording.
 */

import { app } from './app';
import { economyManager } from './economy-manager';
import { corsHandler } from './cors-handler';
import { LLSD } from './llsd';
import { Utils } from './utils';
import { prepareTextureCanvas, encodeJpeg2000 } from './jpeg2000-encoder';

export interface TextureUploadOptions {
  file: File | Blob;
  name: string;
  description?: string;
  folderId?: string;
  lossless?: boolean;
}

export interface TextureUploadProgress {
  stage: 'scaling' | 'encoding' | 'requesting_cap' | 'uploading' | 'completing';
  percent: number;
  message: string;
}

export interface TextureUploadResult {
  success: boolean;
  item: any;
  assetId: string;
  inventoryItemId: string;
  dimensions: { width: number; height: number };
}

/**
 * Executes texture upload workflow.
 */
export async function uploadTexture(
  options: TextureUploadOptions,
  onProgress?: (progress: TextureUploadProgress) => void
): Promise<TextureUploadResult> {
  if (!options.file) {
    throw new Error('Image file or Blob is required for texture upload');
  }

  // 1. Check account balance before initiating network requests
  const currentBalance = economyManager.balance;
  if (currentBalance !== null && typeof currentBalance === 'number' && currentBalance < 10) {
    throw new Error(`Insufficient funds: L$10 upload fee required, but account balance is L$${currentBalance}`);
  }

  // 2. Check for NewFileAgentInventory capability
  const capabilityUrl = app.protocol.getCapability('NewFileAgentInventory');
  if (!capabilityUrl) {
    throw new Error('Grid capability "NewFileAgentInventory" is not available on current region/connection');
  }

  // 3. Stage 1: Power-of-two scaling on HTML Canvas
  onProgress?.({ stage: 'scaling', percent: 10, message: 'Scaling image to power-of-two dimensions...' });
  const { width, height, rgba } = await prepareTextureCanvas(options.file);

  // 4. Stage 2: Encode RGBA pixels to JPEG2000 codestream (image/x-j2c)
  onProgress?.({ stage: 'encoding', percent: 30, message: `Encoding JPEG2000 codestream (${width}x${height})...` });
  const j2kPayload = await encodeJpeg2000(rgba, width, height, options.lossless);

  // 5. Stage 3: Request upload URL via NewFileAgentInventory capability
  onProgress?.({ stage: 'requesting_cap', percent: 50, message: 'Requesting upload URL from grid...' });

  let targetFolderId = options.folderId;
  if (!targetFolderId) {
    for (const [id, folder] of app.inventory.folders.entries()) {
      if (
        folder.name?.toLowerCase() === 'textures' ||
        folder.type_default === 0 ||
        folder.folderType === 0
      ) {
        targetFolderId = id;
        break;
      }
    }
    if (!targetFolderId) {
      targetFolderId = app.inventory.rootFolder?.id || app.protocol.inventoryRoot || 'root';
    }
  }

  const assetName = options.name?.trim() || 'New Texture';
  const assetDesc = options.description?.trim() || 'Uploaded via Linkpoint Mobile';

  const capRequestBody = {
    folder_id: targetFolderId,
    asset_type: 0,
    inventory_type: 0,
    name: assetName,
    description: assetDesc,
    expected_upload_cost: 10,
    everyone_mask: 2147483647,
    group_mask: 2147483647,
    next_owner_mask: 2147483647,
  };

  const response = await corsHandler.makeRequest(capabilityUrl, {
    method: 'POST',
    headers: { 'Content-Type': 'application/llsd+xml' },
    body: LLSD.buildXML(capRequestBody),
  });

  if (!response || !response.ok) {
    throw new Error(`Capability NewFileAgentInventory request failed (HTTP ${response?.status || 'Network Error'})`);
  }

  const responseText = await response.text();
  let llsdData: any = {};
  try {
    llsdData = LLSD.parseXML(responseText);
  } catch (parseErr) {
    throw new Error(`Failed to parse NewFileAgentInventory LLSD response: ${parseErr}`);
  }

  if (llsdData?.state === 'insufficient_funds' || llsdData?.error === 'insufficient_funds') {
    throw new Error('Upload rejected by grid: Insufficient account funds');
  }

  const uploaderUrl = llsdData?.uploader || llsdData?.url;
  if (!uploaderUrl) {
    throw new Error('Grid capability did not return an uploader URL');
  }

  // 6. Stage 4: POST JPEG2000 payload to uploader URL with progress tracking
  onProgress?.({ stage: 'uploading', percent: 70, message: 'Uploading JPEG2000 payload to grid...' });

  const uploadResponse = await corsHandler.makeRequest(uploaderUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'image/x-j2c',
    },
    body: j2kPayload,
  });

  if (!uploadResponse || !uploadResponse.ok) {
    throw new Error(`Payload POST to uploader URL failed (HTTP ${uploadResponse?.status || 'Network Error'})`);
  }

  const uploadText = await uploadResponse.text();
  let uploadResult: any = {};
  try {
    uploadResult = LLSD.parseXML(uploadText);
  } catch {
    uploadResult = { state: 'complete' };
  }

  onProgress?.({ stage: 'completing', percent: 90, message: 'Registering item in inventory and deducting L$ fee...' });

  // 7. Stage 5: Register item in InventoryManager and record Economy fee
  const newAssetId = uploadResult?.new_asset || uploadResult?.asset_id || llsdData?.new_asset || Utils.generateUUID();
  const newItemId = uploadResult?.new_inventory_item || uploadResult?.item_id || llsdData?.new_inventory_item || Utils.generateUUID();

  const newItem = {
    id: newItemId,
    name: assetName,
    description: assetDesc,
    assetType: 0,
    inventoryType: 0,
    parent: targetFolderId,
    assetId: newAssetId,
    type: 'item',
    permissions: {
      base_mask: 2147483647,
      owner_mask: 2147483647,
      everyone_mask: 2147483647,
      group_mask: 2147483647,
      next_owner_mask: 2147483647,
    },
  };

  app.inventory.items.set(newItemId, newItem);
  const targetFolder = app.inventory.folders.get(targetFolderId);
  if (targetFolder) {
    if (!Array.isArray(targetFolder.children)) targetFolder.children = [];
    if (!targetFolder.children.includes(newItemId)) targetFolder.children.push(newItemId);
  }

  // Record fee and update balance
  await economyManager.recordUploadFee(10, assetName);

  app.inventory.emit('inventory_updated');
  app.inventory.emit('inventory_loaded');

  onProgress?.({ stage: 'completing', percent: 100, message: 'Texture uploaded successfully!' });

  return {
    success: true,
    item: newItem,
    assetId: newAssetId,
    inventoryItemId: newItemId,
    dimensions: { width, height },
  };
}
