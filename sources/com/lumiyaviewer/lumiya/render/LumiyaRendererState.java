package com.lumiyaviewer.lumiya.render;

/**
 * This class holds a static reference to the active WorldViewRenderer.
 * This is a pragmatic approach to allow access to the renderer from other threads
 * (like the network thread in SLAgentCircuit) in a codebase that does not use
 * dependency injection. This should be used with caution.
 */
public class LumiyaRendererState {
    public static volatile WorldViewRenderer instance = null;
}
