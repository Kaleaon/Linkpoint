import type { ViewerCommand, ViewerEvent } from "../../viewer-types/src/index";

export type Unsubscribe = () => void;

/** The only simulator/runtime boundary exposed to application code. */
export interface ViewerClient {
  execute(command: ViewerCommand): Promise<void>;
  subscribe(listener: (event: ViewerEvent) => void): Unsubscribe;
}

/** Deterministic client for screen development and contract tests. */
export class MockViewerClient implements ViewerClient {
  readonly commands: ViewerCommand[] = [];
  readonly #listeners = new Set<(event: ViewerEvent) => void>();

  async execute(command: ViewerCommand): Promise<void> {
    this.commands.push(command);
  }

  subscribe(listener: (event: ViewerEvent) => void): Unsubscribe {
    this.#listeners.add(listener);
    return () => this.#listeners.delete(listener);
  }

  emit(event: ViewerEvent): void {
    for (const listener of this.#listeners) listener(event);
  }
}
