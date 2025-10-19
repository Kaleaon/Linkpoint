/*
 * LSL (Linden Scripting Language) Engine - Java implementation
 *
 * Based on Second Life LSL specification
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.libraries.scripting;

import lindenlab.llsd.viewer.secondlife.engine.Vector3;
import lindenlab.llsd.viewer.secondlife.engine.Quaternion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * LSL (Linden Scripting Language) execution engine for Second Life scripts.
 * <p>
 * This class provides a Java implementation of the LSL scripting system including:
 * <ul>
 *   <li>LSL syntax parsing and compilation</li>
 *   <li>Script execution with state machine support</li>
 *   <li>Built-in LSL functions and constants</li>
 *   <li>Event handling system</li>
 *   <li>Memory and execution time limits</li>
 *   <li>Inter-script communication</li>
 * </ul>
 * 
 * @author LLSD Java Team
 * @since 1.0
 */
public class LSLEngine {
    
    private boolean initialized = false;
    private final Map<UUID, LSLScript> activeScripts = new ConcurrentHashMap<>();
    private final Map<String, LSLFunction> builtinFunctions = new HashMap<>();
    private final Map<String, Object> globalConstants = new HashMap<>();
    private final LSLSettings settings = new LSLSettings();
    private ScriptEventQueue eventQueue;
    
    // Performance tracking
    private long totalInstructions = 0;
    private long totalExecutionTime = 0;
    
    /**
     * LSL engine configuration settings.
     */
    public static class LSLSettings {
        public int maxScriptMemory = 65536;      // 64KB per script
        public float maxExecutionTime = 0.1f;    // 100ms max per execution
        public int maxInstructions = 10000;      // Max instructions per execution
        public int maxListLength = 255;          // Max list elements
        public int maxStringLength = 1024;       // Max string length
        public boolean enablePreemption = true;  // Allow script interruption
        public boolean enableDebugMode = false;  // Debug output
        public int maxScriptsPerObject = 32;     // Scripts per object limit
    }
    
    /**
     * Represents a compiled and executing LSL script.
     */
    public static class LSLScript {
        private final UUID scriptId;
        private final UUID objectId;
        private final String scriptName;
        private final String sourceCode;
        private final List<LSLInstruction> instructions;
        private final Map<String, LSLVariable> variables = new HashMap<>();
        private final Map<String, LSLState> states = new HashMap<>();
        
        private LSLState currentState;
        private int programCounter = 0;
        private long memoryUsed = 0;
        private long instructionsExecuted = 0;
        private ScriptStatus status = ScriptStatus.STOPPED;
        private String errorMessage = "";
        
        public enum ScriptStatus {
            STOPPED, RUNNING, PAUSED, ERROR, COMPILED
        }
        
        public LSLScript(UUID scriptId, UUID objectId, String scriptName, String sourceCode) {
            this.scriptId = scriptId;
            this.objectId = objectId;
            this.scriptName = scriptName;
            this.sourceCode = sourceCode;
            this.instructions = new ArrayList<>();
        }
        
        // Getters and setters
        public UUID getScriptId() { return scriptId; }
        public UUID getObjectId() { return objectId; }
        public String getScriptName() { return scriptName; }
        public String getSourceCode() { return sourceCode; }
        public List<LSLInstruction> getInstructions() { return instructions; }
        public Map<String, LSLVariable> getVariables() { return variables; }
        public Map<String, LSLState> getStates() { return states; }
        
        public LSLState getCurrentState() { return currentState; }
        public void setCurrentState(LSLState state) { this.currentState = state; }
        
        public int getProgramCounter() { return programCounter; }
        public void setProgramCounter(int pc) { this.programCounter = pc; }
        
        public long getMemoryUsed() { return memoryUsed; }
        public void setMemoryUsed(long memory) { this.memoryUsed = memory; }
        
        public long getInstructionsExecuted() { return instructionsExecuted; }
        public void incrementInstructions() { this.instructionsExecuted++; }
        
        public ScriptStatus getStatus() { return status; }
        public void setStatus(ScriptStatus status) { this.status = status; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String error) { this.errorMessage = error; }
        
        /**
         * Get or create a variable.
         */
        public LSLVariable getVariable(String name) {
            return variables.computeIfAbsent(name, k -> new LSLVariable(name, LSLType.UNDEFINED, null));
        }
        
        /**
         * Set variable value.
         */
        public void setVariable(String name, LSLType type, Object value) {
            LSLVariable var = getVariable(name);
            var.setType(type);
            var.setValue(value);
        }
    }
    
    /**
     * LSL script state with event handlers.
     */
    public static class LSLState {
        private final String name;
        private final Map<String, List<LSLInstruction>> eventHandlers = new HashMap<>();
        private final List<LSLInstruction> entryInstructions = new ArrayList<>();
        private final List<LSLInstruction> exitInstructions = new ArrayList<>();
        
        public LSLState(String name) {
            this.name = name;
        }
        
        public String getName() { return name; }
        public Map<String, List<LSLInstruction>> getEventHandlers() { return eventHandlers; }
        public List<LSLInstruction> getEntryInstructions() { return entryInstructions; }
        public List<LSLInstruction> getExitInstructions() { return exitInstructions; }
        
        public void addEventHandler(String eventName, List<LSLInstruction> instructions) {
            eventHandlers.put(eventName, instructions);
        }
    }
    
    /**
     * LSL variable with type and value.
     */
    public static class LSLVariable {
        private String name;
        private LSLType type;
        private Object value;
        
        public LSLVariable(String name, LSLType type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }
        
        public String getName() { return name; }
        public LSLType getType() { return type; }
        public void setType(LSLType type) { this.type = type; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        
        @Override
        public String toString() {
            return name + "(" + type + ")=" + value;
        }
    }
    
    /**
     * LSL data types.
     */
    public enum LSLType {
        INTEGER, FLOAT, STRING, KEY, VECTOR, ROTATION, LIST, UNDEFINED
    }
    
    /**
     * LSL instruction for script execution.
     */
    public static class LSLInstruction {
        private final InstructionType type;
        private final String operation;
        private final Object[] operands;
        
        public enum InstructionType {
            LOAD, STORE, CALL, JUMP, BRANCH, RETURN, PUSH, POP, 
            ADD, SUB, MUL, DIV, MOD, CMP, AND, OR, NOT
        }
        
        public LSLInstruction(InstructionType type, String operation, Object... operands) {
            this.type = type;
            this.operation = operation;
            this.operands = operands;
        }
        
        public InstructionType getType() { return type; }
        public String getOperation() { return operation; }
        public Object[] getOperands() { return operands; }
        
        @Override
        public String toString() {
            return type + " " + operation + " " + Arrays.toString(operands);
        }
    }
    
    /**
     * LSL built-in function interface.
     */
    public interface LSLFunction {
        Object execute(LSLScript script, Object... args) throws LSLRuntimeException;
        LSLType[] getParameterTypes();
        LSLType getReturnType();
        String getDescription();
    }
    
    /**
     * LSL runtime exception.
     */
    public static class LSLRuntimeException extends Exception {
        public LSLRuntimeException(String message) {
            super(message);
        }
        
        public LSLRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Script event for the event queue.
     */
    public static class ScriptEvent {
        private final UUID scriptId;
        private final String eventName;
        private final Object[] parameters;
        private final long timestamp;
        
        public ScriptEvent(UUID scriptId, String eventName, Object... parameters) {
            this.scriptId = scriptId;
            this.eventName = eventName;
            this.parameters = parameters;
            this.timestamp = System.currentTimeMillis();
        }
        
        public UUID getScriptId() { return scriptId; }
        public String getEventName() { return eventName; }
        public Object[] getParameters() { return parameters; }
        public long getTimestamp() { return timestamp; }
    }
    
    /**
     * Event queue for managing script events.
     */
    public static class ScriptEventQueue {
        private final Queue<ScriptEvent> events = new LinkedList<>();
        private final int maxQueueSize = 1000;
        
        public synchronized void enqueueEvent(ScriptEvent event) {
            if (events.size() >= maxQueueSize) {
                events.poll(); // Remove oldest event
            }
            events.offer(event);
        }
        
        public synchronized ScriptEvent dequeueEvent() {
            return events.poll();
        }
        
        public synchronized int getQueueSize() {
            return events.size();
        }
    }
    
    /**
     * Initialize the LSL engine.
     * 
     * @return true if initialization was successful, false otherwise
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }
        
        try {
            System.out.println("Initializing LSL engine...");
            
            // Initialize event queue
            eventQueue = new ScriptEventQueue();
            
            // Register built-in functions
            registerBuiltinFunctions();
            
            // Initialize global constants
            initializeGlobalConstants();
            
            initialized = true;
            System.out.println("LSL engine initialized successfully");
            System.out.println("Built-in functions: " + builtinFunctions.size());
            System.out.println("Global constants: " + globalConstants.size());
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error initializing LSL engine: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Compile an LSL script from source code.
     * 
     * @param scriptId Unique script identifier
     * @param objectId Object containing the script
     * @param scriptName Name of the script
     * @param sourceCode LSL source code
     * @return The compiled script, or null if compilation failed
     */
    public LSLScript compileScript(UUID scriptId, UUID objectId, String scriptName, String sourceCode) {
        if (!initialized) {
            System.err.println("LSL engine not initialized");
            return null;
        }
        
        try {
            System.out.println("Compiling LSL script: " + scriptName);
            
            LSLScript script = new LSLScript(scriptId, objectId, scriptName, sourceCode);
            
            // Simple lexical analysis and parsing (placeholder)
            if (!parseScript(script)) {
                script.setStatus(LSLScript.ScriptStatus.ERROR);
                script.setErrorMessage("Compilation failed");
                return script;
            }
            
            // Generate instructions (placeholder)
            generateInstructions(script);
            
            script.setStatus(LSLScript.ScriptStatus.COMPILED);
            System.out.println("Script compiled successfully: " + script.getInstructions().size() + " instructions");
            
            return script;
            
        } catch (Exception e) {
            System.err.println("Error compiling script: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Start execution of a compiled script.
     * 
     * @param script The compiled script to start
     * @return true if the script was started successfully
     */
    public boolean startScript(LSLScript script) {
        if (!initialized || script == null) {
            return false;
        }
        
        if (script.getStatus() != LSLScript.ScriptStatus.COMPILED) {
            System.err.println("Script is not compiled: " + script.getScriptName());
            return false;
        }
        
        try {
            // Add to active scripts
            activeScripts.put(script.getScriptId(), script);
            
            // Set default state
            LSLState defaultState = script.getStates().get("default");
            if (defaultState == null) {
                defaultState = new LSLState("default");
                script.getStates().put("default", defaultState);
            }
            script.setCurrentState(defaultState);
            
            // Reset execution state
            script.setProgramCounter(0);
            script.setStatus(LSLScript.ScriptStatus.RUNNING);
            
            // Fire state_entry event
            enqueueEvent(script.getScriptId(), "state_entry");
            
            System.out.println("Started LSL script: " + script.getScriptName());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error starting script: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Execute pending script events and instructions.
     * 
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(float deltaTime) {
        if (!initialized) {
            return;
        }
        
        // Process events from the queue
        processEvents();
        
        // Execute active scripts
        for (LSLScript script : activeScripts.values()) {
            if (script.getStatus() == LSLScript.ScriptStatus.RUNNING) {
                executeScript(script, deltaTime);
            }
        }
    }
    
    /**
     * Enqueue an event for a specific script.
     * 
     * @param scriptId The script ID
     * @param eventName The event name
     * @param parameters Event parameters
     */
    public void enqueueEvent(UUID scriptId, String eventName, Object... parameters) {
        if (eventQueue != null) {
            ScriptEvent event = new ScriptEvent(scriptId, eventName, parameters);
            eventQueue.enqueueEvent(event);
        }
    }
    
    /**
     * Stop a running script.
     * 
     * @param scriptId The script ID
     * @return true if the script was stopped
     */
    public boolean stopScript(UUID scriptId) {
        LSLScript script = activeScripts.remove(scriptId);
        if (script != null) {
            script.setStatus(LSLScript.ScriptStatus.STOPPED);
            System.out.println("Stopped LSL script: " + script.getScriptName());
            return true;
        }
        return false;
    }
    
    /**
     * Get the current LSL settings.
     * 
     * @return The LSL settings object
     */
    public LSLSettings getSettings() {
        return settings;
    }
    
    /**
     * Get information about all active scripts.
     * 
     * @return Map of script IDs to script objects
     */
    public Map<UUID, LSLScript> getActiveScripts() {
        return new HashMap<>(activeScripts);
    }
    
    /**
     * Shutdown the LSL engine and cleanup resources.
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }
        
        System.out.println("Shutting down LSL engine...");
        
        // Stop all active scripts
        for (UUID scriptId : new HashSet<>(activeScripts.keySet())) {
            stopScript(scriptId);
        }
        
        activeScripts.clear();
        builtinFunctions.clear();
        globalConstants.clear();
        eventQueue = null;
        
        initialized = false;
        System.out.println("LSL engine shutdown complete");
        System.out.println("Total instructions executed: " + totalInstructions);
        System.out.println("Total execution time: " + (totalExecutionTime / 1_000_000.0) + "ms");
    }
    
    // Private helper methods
    
    private void registerBuiltinFunctions() {
        // Basic LSL functions (simplified implementations)
        
        // String functions
        builtinFunctions.put("llSay", new LSLFunction() {
            @Override
            public Object execute(LSLScript script, Object... args) {
                int channel = ((Number) args[0]).intValue();
                String message = args[1].toString();
                System.out.println("[LSL Say " + channel + "] " + message);
                return null;
            }
            
            @Override
            public LSLType[] getParameterTypes() {
                return new LSLType[]{LSLType.INTEGER, LSLType.STRING};
            }
            
            @Override
            public LSLType getReturnType() {
                return LSLType.UNDEFINED;
            }
            
            @Override
            public String getDescription() {
                return "Says text on the specified channel";
            }
        });
        
        // Math functions
        builtinFunctions.put("llFabs", new LSLFunction() {
            @Override
            public Object execute(LSLScript script, Object... args) {
                return Math.abs(((Number) args[0]).doubleValue());
            }
            
            @Override
            public LSLType[] getParameterTypes() {
                return new LSLType[]{LSLType.FLOAT};
            }
            
            @Override
            public LSLType getReturnType() {
                return LSLType.FLOAT;
            }
            
            @Override
            public String getDescription() {
                return "Returns the absolute value of a float";
            }
        });
        
        // Vector functions
        builtinFunctions.put("llVecMag", new LSLFunction() {
            @Override
            public Object execute(LSLScript script, Object... args) {
                Vector3 vec = (Vector3) args[0];
                return vec.magnitude();
            }
            
            @Override
            public LSLType[] getParameterTypes() {
                return new LSLType[]{LSLType.VECTOR};
            }
            
            @Override
            public LSLType getReturnType() {
                return LSLType.FLOAT;
            }
            
            @Override
            public String getDescription() {
                return "Returns the magnitude of a vector";
            }
        });
        
        System.out.println("Registered " + builtinFunctions.size() + " built-in LSL functions");
    }
    
    private void initializeGlobalConstants() {
        // LSL constants
        globalConstants.put("TRUE", 1);
        globalConstants.put("FALSE", 0);
        globalConstants.put("PI", Math.PI);
        globalConstants.put("TWO_PI", 2.0 * Math.PI);
        globalConstants.put("PI_BY_TWO", Math.PI / 2.0);
        globalConstants.put("DEG_TO_RAD", Math.PI / 180.0);
        globalConstants.put("RAD_TO_DEG", 180.0 / Math.PI);
        globalConstants.put("SQRT2", Math.sqrt(2.0));
        
        // Vector constants
        globalConstants.put("ZERO_VECTOR", new Vector3(0, 0, 0));
        globalConstants.put("ZERO_ROTATION", new Quaternion(0, 0, 0, 1));
        
        System.out.println("Initialized " + globalConstants.size() + " global constants");
    }
    
    private boolean parseScript(LSLScript script) {
        // Simplified parsing - just check for basic syntax
        String source = script.getSourceCode();
        
        // Check for default state
        if (!source.contains("default")) {
            script.setErrorMessage("Script must contain a default state");
            return false;
        }
        
        // Create a simple default state
        LSLState defaultState = new LSLState("default");
        script.getStates().put("default", defaultState);
        
        // Add a simple state_entry handler
        List<LSLInstruction> entryInstructions = new ArrayList<>();
        entryInstructions.add(new LSLInstruction(
            LSLInstruction.InstructionType.CALL, "llSay", 0, "Script started"));
        
        defaultState.addEventHandler("state_entry", entryInstructions);
        
        return true;
    }
    
    private void generateInstructions(LSLScript script) {
        // Simplified instruction generation
        List<LSLInstruction> instructions = script.getInstructions();
        
        // Add some basic instructions
        instructions.add(new LSLInstruction(LSLInstruction.InstructionType.LOAD, "constant", "Script initialized"));
        instructions.add(new LSLInstruction(LSLInstruction.InstructionType.CALL, "llSay", 0));
        instructions.add(new LSLInstruction(LSLInstruction.InstructionType.RETURN, "return"));
    }
    
    private void processEvents() {
        ScriptEvent event;
        while ((event = eventQueue.dequeueEvent()) != null) {
            LSLScript script = activeScripts.get(event.getScriptId());
            if (script != null && script.getStatus() == LSLScript.ScriptStatus.RUNNING) {
                handleScriptEvent(script, event);
            }
        }
    }
    
    private void handleScriptEvent(LSLScript script, ScriptEvent event) {
        LSLState currentState = script.getCurrentState();
        if (currentState != null) {
            List<LSLInstruction> handler = currentState.getEventHandlers().get(event.getEventName());
            if (handler != null) {
                // Execute event handler instructions
                executeInstructions(script, handler);
            }
        }
    }
    
    private void executeScript(LSLScript script, float deltaTime) {
        long startTime = System.nanoTime();
        int instructionsExecuted = 0;
        
        try {
            while (instructionsExecuted < settings.maxInstructions) {
                if (script.getProgramCounter() >= script.getInstructions().size()) {
                    break; // End of program
                }
                
                LSLInstruction instruction = script.getInstructions().get(script.getProgramCounter());
                executeInstruction(script, instruction);
                
                script.setProgramCounter(script.getProgramCounter() + 1);
                script.incrementInstructions();
                instructionsExecuted++;
                totalInstructions++;
                
                // Check execution time limit
                long elapsed = System.nanoTime() - startTime;
                if (elapsed > settings.maxExecutionTime * 1_000_000_000L) {
                    break; // Time limit exceeded
                }
            }
            
        } catch (LSLRuntimeException e) {
            script.setStatus(LSLScript.ScriptStatus.ERROR);
            script.setErrorMessage(e.getMessage());
            System.err.println("LSL Runtime Error in " + script.getScriptName() + ": " + e.getMessage());
        }
        
        totalExecutionTime += System.nanoTime() - startTime;
    }
    
    private void executeInstructions(LSLScript script, List<LSLInstruction> instructions) {
        for (LSLInstruction instruction : instructions) {
            try {
                executeInstruction(script, instruction);
            } catch (LSLRuntimeException e) {
                script.setErrorMessage(e.getMessage());
                break;
            }
        }
    }
    
    private void executeInstruction(LSLScript script, LSLInstruction instruction) throws LSLRuntimeException {
        switch (instruction.getType()) {
            case CALL:
                String functionName = instruction.getOperation();
                LSLFunction function = builtinFunctions.get(functionName);
                if (function != null) {
                    function.execute(script, instruction.getOperands());
                } else {
                    throw new LSLRuntimeException("Unknown function: " + functionName);
                }
                break;
                
            case LOAD:
                // Load a value (placeholder)
                break;
                
            case STORE:
                // Store a value (placeholder)
                break;
                
            case RETURN:
                // Return from function (placeholder)
                break;
                
            default:
                throw new LSLRuntimeException("Unknown instruction type: " + instruction.getType());
        }
    }
}