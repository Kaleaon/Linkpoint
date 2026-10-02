/**
 * Agent Inventory Service (AIS v3) REST Client for Second Life Outfit Swaps
 */

import { LLSD } from './llsd';
import { corsHandler } from './cors-handler';

export interface AisItemLink {
  item_id: string;
}

export interface AisOutfitPayload {
  folder_id: string;
  items: AisItemLink[];
}

export interface AisResponse {
  success: boolean;
  status: number;
  data?: any;
  error?: string;
  usedAis: boolean;
}

export class AisClient {
  private capabilitiesProvider: () => Record<string, string>;
  private refreshCapsCallback?: () => Promise<Record<string, string>>;

  constructor(
    capabilitiesProvider: () => Record<string, string>,
    refreshCapsCallback?: () => Promise<Record<string, string>>
  ) {
    this.capabilitiesProvider = capabilitiesProvider;
    this.refreshCapsCallback = refreshCapsCallback;
  }

  /**
   * Get the active Agent Inventory Service capability URL if available.
   */
  public getAisCapabilityUrl(): string | null {
    const caps = this.capabilitiesProvider() || {};
    return caps['AgentInventoryService'] || caps['AgentInventoryService3'] || null;
  }

  /**
   * Check if the connected grid supports AIS v3 endpoints.
   */
  public hasAisCapability(): boolean {
    return Boolean(this.getAisCapabilityUrl());
  }

  /**
   * Perform an HTTP REST request with exponential backoff and 401/403 token refresh.
   */
  public async makeAisRequest(
    pathSuffix: string,
    method: 'POST' | 'PUT' | 'DELETE' | 'GET',
    payload?: any,
    maxRetries = 3
  ): Promise<AisResponse> {
    let aisUrl = this.getAisCapabilityUrl();
    if (!aisUrl) {
      return {
        success: false,
        status: 0,
        error: 'AIS v3 capability is not available on this grid',
        usedAis: false,
      };
    }

    let url = `${aisUrl}${pathSuffix}`;
    let attempts = 0;
    let delay = 300;

    while (attempts < maxRetries) {
      attempts++;
      try {
        const headers: Record<string, string> = {
          'Content-Type': 'application/llsd+xml',
          'Accept': 'application/llsd+xml, application/xml, text/xml',
        };

        const body = payload ? LLSD.buildXML(payload) : undefined;

        const response = await corsHandler.makeRequest(url, {
          method,
          headers,
          body,
        });

        if (response) {
          if (response.ok) {
            let data: any = null;
            try {
              const text = await response.text();
              if (text && text.trim()) {
                data = LLSD.parseXML(text);
              }
            } catch {
              // LLSD parsing optional for simple 200/201 OK responses
            }
            return {
              success: true,
              status: response.status,
              data,
              usedAis: true,
            };
          }

          // Handle 401 or 403 (session/token expired) with token refresh retry
          if ((response.status === 401 || response.status === 403) && this.refreshCapsCallback) {
            console.warn('[AIS] Session token expired (HTTP ' + response.status + '). Refreshing capabilities...');
            const freshCaps = await this.refreshCapsCallback();
            const newAisUrl = freshCaps['AgentInventoryService'] || freshCaps['AgentInventoryService3'];
            if (newAisUrl) {
              aisUrl = newAisUrl;
              url = `${aisUrl}${pathSuffix}`;
            }
          }

          // Retry on 5xx server errors or 401/403 token refresh
          if (response.status >= 500 || response.status === 401 || response.status === 403) {
            if (attempts < maxRetries) {
              await new Promise((resolve) => setTimeout(resolve, delay));
              delay *= 2;
              continue;
            }
          }

          return {
            success: false,
            status: response.status,
            error: `AIS REST request failed with HTTP ${response.status}`,
            usedAis: true,
          };
        }
      } catch (err: any) {
        if (attempts >= maxRetries) {
          return {
            success: false,
            status: 0,
            error: err?.message || 'AIS network request error',
            usedAis: true,
          };
        }
      }

      await new Promise((resolve) => setTimeout(resolve, delay));
      delay *= 2;
    }

    return {
      success: false,
      status: 0,
      error: 'AIS REST request retries exhausted',
      usedAis: true,
    };
  }

  /**
   * Atomic Replace Outfit via AIS REST endpoint:
   * Replaces all items in Current Outfit Folder (COF) with items from target outfit folder.
   */
  public async replaceOutfit(
    cofFolderId: string,
    outfitFolderId: string,
    itemIds: string[]
  ): Promise<AisResponse> {
    const itemsPayload: AisItemLink[] = itemIds.map((id) => ({ item_id: id }));
    const payload: AisOutfitPayload = {
      folder_id: outfitFolderId,
      items: itemsPayload,
    };

    const endpoint = `/category/${cofFolderId}?op=replace`;
    return this.makeAisRequest(endpoint, 'POST', payload);
  }

  /**
   * Append / Add to Outfit via AIS REST endpoint:
   * Links items from target outfit folder into Current Outfit Folder (COF) without removing existing COF items.
   */
  public async addToOutfit(
    cofFolderId: string,
    outfitFolderId: string,
    itemIds: string[]
  ): Promise<AisResponse> {
    const itemsPayload: AisItemLink[] = itemIds.map((id) => ({ item_id: id }));
    const payload: AisOutfitPayload = {
      folder_id: outfitFolderId,
      items: itemsPayload,
    };

    const endpoint = `/category/${cofFolderId}/array_links`;
    return this.makeAisRequest(endpoint, 'POST', payload);
  }
}
