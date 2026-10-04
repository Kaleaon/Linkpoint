/**
 * Linkpoint - Circuit Context Manager
 * 
 * Circuit identity tracking and AgentData parameter management for SL/OpenSim grid protocol compliance.
 */

export interface CircuitParams {
  agentId: string | null;
  sessionId: string | null;
  circuitCode: number | null;
  secureSessionId?: string | null;
  regionHandle?: string | null;
  seedCapability?: string | null;
  regionName?: string | null;
}

const NULL_UUID = '00000000-0000-0000-0000-000000000000';

export class CircuitContextManager {
  private agentId: string | null = null;
  private sessionId: string | null = null;
  private circuitCode: number | null = null;
  private secureSessionId: string | null = null;
  private regionHandle: string | null = null;
  private seedCapability: string | null = null;
  private regionName: string | null = null;
  private isDisconnected: boolean = false;

  constructor(initialParams?: Partial<CircuitParams>) {
    if (initialParams) {
      this.updateCircuit(initialParams);
    }
  }

  /** Update active circuit identity parameters. */
  public updateCircuit(params: Partial<CircuitParams>): void {
    if (params.agentId !== undefined && params.agentId !== null) {
      this.agentId = String(params.agentId).trim();
    }
    if (params.sessionId !== undefined && params.sessionId !== null) {
      this.sessionId = String(params.sessionId).trim();
    }
    if (params.circuitCode !== undefined && params.circuitCode !== null) {
      const code = Number(params.circuitCode);
      this.circuitCode = Number.isFinite(code) && code > 0 ? code : null;
    }
    if (params.secureSessionId !== undefined) {
      this.secureSessionId = params.secureSessionId ? String(params.secureSessionId).trim() : null;
    }
    if (params.regionHandle !== undefined) {
      this.regionHandle = params.regionHandle ? String(params.regionHandle).trim() : null;
    }
    if (params.seedCapability !== undefined) {
      this.seedCapability = params.seedCapability ? String(params.seedCapability).trim() : null;
    }
    if (params.regionName !== undefined) {
      this.regionName = params.regionName ? String(params.regionName).trim() : null;
    }
    this.isDisconnected = false;
  }

  /** Return the active circuit parameters snapshot. */
  public getCircuitContext(): CircuitParams {
    return {
      agentId: this.agentId,
      sessionId: this.sessionId,
      circuitCode: this.circuitCode,
      secureSessionId: this.secureSessionId,
      regionHandle: this.regionHandle,
      seedCapability: this.seedCapability,
      regionName: this.regionName,
    };
  }

  /** Returns true if agentId, sessionId, and circuitCode are all non-zero and valid. */
  public hasValidCircuit(): boolean {
    const validAgent = Boolean(this.agentId && this.agentId !== NULL_UUID && this.agentId !== '0');
    const validSession = Boolean(this.sessionId && this.sessionId !== NULL_UUID && this.sessionId !== '0');
    const validCode = Boolean(this.circuitCode && this.circuitCode > 0);
    return validAgent && validSession && validCode;
  }

  /** Build standard AgentData block. */
  public buildAgentData(): { AgentID: string; SessionID: string; CircuitCode: number } {
    return {
      AgentID: this.agentId ? this.agentId : NULL_UUID,
      SessionID: this.sessionId ? this.sessionId : NULL_UUID,
      CircuitCode: typeof this.circuitCode === 'number' ? this.circuitCode : 0,
    };
  }

  /**
   * Attaches active circuit identity parameters (AgentID, SessionID, CircuitCode)
   * to outgoing UDP packet or LLSD request payloads, ensuring zeroed blocks are replaced.
   */
  public attachAgentData<T extends Record<string, any>>(
    payload: T
  ): T & { AgentData: { AgentID: string; SessionID: string; CircuitCode: number } } {
    if (!payload || typeof payload !== 'object') {
      return payload as any;
    }

    const agentDataBlock = this.buildAgentData();
    const result: Record<string, any> = { ...payload };

    if (Array.isArray(result.AgentData)) {
      result.AgentData = result.AgentData.map((item: any) => {
        if (!item || typeof item !== 'object') return agentDataBlock;
        const id = item.AgentID || item.agent_id || item.agentId;
        const sess = item.SessionID || item.session_id || item.sessionId;
        const code = item.CircuitCode || item.circuit_code || item.circuitCode;
        return {
          ...item,
          AgentID: id && id !== NULL_UUID && id !== '0' ? id : agentDataBlock.AgentID,
          SessionID: sess && sess !== NULL_UUID && sess !== '0' ? sess : agentDataBlock.SessionID,
          CircuitCode: code && Number(code) > 0 ? Number(code) : agentDataBlock.CircuitCode,
        };
      });
    } else if (result.AgentData && typeof result.AgentData === 'object') {
      const existing = result.AgentData;
      const id = existing.AgentID || existing.agent_id || existing.agentId;
      const sess = existing.SessionID || existing.session_id || existing.sessionId;
      const code = existing.CircuitCode || existing.circuit_code || existing.circuitCode;
      result.AgentData = {
        ...existing,
        AgentID: id && id !== NULL_UUID && id !== '0' ? id : agentDataBlock.AgentID,
        SessionID: sess && sess !== NULL_UUID && sess !== '0' ? sess : agentDataBlock.SessionID,
        CircuitCode: code && Number(code) > 0 ? Number(code) : agentDataBlock.CircuitCode,
      };
    } else {
      result.AgentData = agentDataBlock;
    }

    return result as any;
  }

  /**
   * Validates that mutating requests contain valid non-zero AgentData blocks.
   * Throws an error if circuit identity is invalid/zeroed.
   */
  public validateMutatingRequest(payload?: any): void {
    if (!this.hasValidCircuit()) {
      throw new Error('Invalid or zeroed circuit identity parameters in AgentData block');
    }
    if (payload && typeof payload === 'object') {
      const agentData = payload.AgentData || payload;
      if (Array.isArray(agentData)) {
        for (const item of agentData) {
          this.checkAgentDataFields(item);
        }
      } else {
        this.checkAgentDataFields(agentData);
      }
    }
  }

  private checkAgentDataFields(item: any): void {
    if (!item || typeof item !== 'object') return;
    const agentId = item.AgentID || item.agent_id || item.agentId || this.agentId;
    const sessionId = item.SessionID || item.session_id || item.sessionId || this.sessionId;
    const circuitCode = item.CircuitCode || item.circuit_code || item.circuitCode || this.circuitCode;

    if (!agentId || agentId === NULL_UUID || agentId === '0') {
      throw new Error('Zeroed AgentID in mutating request');
    }
    if (!sessionId || sessionId === NULL_UUID || sessionId === '0') {
      throw new Error('Zeroed SessionID in mutating request');
    }
    if (!circuitCode || Number(circuitCode) <= 0) {
      throw new Error('Zeroed CircuitCode in mutating request');
    }
  }

  /** Mark network as temporarily disconnected while maintaining user session state. */
  public handleNetworkDisconnect(): void {
    this.isDisconnected = true;
  }

  /** Mark network as reconnected. */
  public handleNetworkReconnect(): void {
    this.isDisconnected = false;
  }

  public getDisconnectedState(): boolean {
    return this.isDisconnected;
  }

  /** Reset all circuit parameters on logout or explicit disconnect. */
  public reset(): void {
    this.agentId = null;
    this.sessionId = null;
    this.circuitCode = null;
    this.secureSessionId = null;
    this.regionHandle = null;
    this.seedCapability = null;
    this.regionName = null;
    this.isDisconnected = false;
  }
}
