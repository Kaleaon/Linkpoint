package com.linkpoint.world

import android.content.Context
import android.util.Log
import org.w3c.dom.Element
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Tree Species Manager - Manages tree/foliage species definitions.
 * 
 * Based on Second Life's procedural tree system. Each species defines
 * parameters for procedural generation including branching, foliage,
 * and LOD billboard properties.
 */
class TreeSpeciesManager(context: Context?) {
    
    companion object {
        private const val TAG = "TreeSpecies"
        
        // Default tree species IDs from Second Life
        const val SPECIES_PINE_1 = 0
        const val SPECIES_OAK = 1
        const val SPECIES_TROPICAL_BUSH_1 = 2
        const val SPECIES_PALM_1 = 3
        const val SPECIES_DOGWOOD = 4
        const val SPECIES_TROPICAL_BUSH_2 = 5
        const val SPECIES_PALM_2 = 6
        const val SPECIES_CYPRESS_1 = 7
        const val SPECIES_CYPRESS_2 = 8
        const val SPECIES_PINE_2 = 9
        const val SPECIES_PLUMERIA = 10
        const val SPECIES_WINTER_PINE_1 = 11
        const val SPECIES_WINTER_ASPEN = 12
        const val SPECIES_WINTER_PINE_2 = 13
        const val SPECIES_EUCALYPTUS = 14
        const val SPECIES_FERN = 15
        const val SPECIES_EELGRASS = 16
        const val SPECIES_SEA_SWORD = 17
        const val SPECIES_KELP_1 = 18
        const val SPECIES_BEACH_GRASS_1 = 19
        const val SPECIES_KELP_2 = 20
    }
    
    /**
     * Tree species definition with procedural generation parameters.
     */
    data class TreeSpecies(
        val speciesId: Int,
        val name: String,
        val textureId: UUID,
        val droop: Float,           // Branch droop angle (degrees)
        val twist: Float,           // Branch rotation variation
        val branches: Float,        // Branches per level
        val depth: Int,             // Branching recursion depth
        val scaleStep: Float,       // Size reduction per level
        val trunkDepth: Int,        // Levels that form trunk
        val branchLength: Float,    // Base branch length
        val trunkLength: Float,     // Main trunk height
        val leafScale: Float,       // Leaf cluster size
        val billboardScale: Float,  // LOD billboard size
        val billboardRatio: Float,  // Billboard aspect ratio
        val trunkAspect: Float,     // Trunk width/length ratio
        val branchAspect: Float,    // Branch width/length ratio
        val leafRotate: Float,      // Leaf rotation variation
        val noiseMag: Float,        // Random noise magnitude
        val noiseScale: Float,      // Noise pattern scale
        val taper: Float,           // Trunk/branch taper
        val repeatZ: Int            // Vertical texture repeats
    ) {
        /**
         * Check if this is an underwater/aquatic plant
         */
        val isAquatic: Boolean
            get() = speciesId in setOf(SPECIES_EELGRASS, SPECIES_SEA_SWORD, SPECIES_KELP_1, SPECIES_KELP_2)
        
        /**
         * Check if this is a grass/ground cover plant
         */
        val isGroundCover: Boolean
            get() = speciesId in setOf(SPECIES_FERN, SPECIES_BEACH_GRASS_1, SPECIES_TROPICAL_BUSH_1, SPECIES_TROPICAL_BUSH_2)
        
        /**
         * Check if this is a winter/snowy variant
         */
        val isWinter: Boolean
            get() = speciesId in setOf(SPECIES_WINTER_PINE_1, SPECIES_WINTER_PINE_2, SPECIES_WINTER_ASPEN)
        
        /**
         * Get total tree height for LOD calculations
         */
        val estimatedHeight: Float
            get() = trunkLength + (branchLength * depth * scaleStep)
    }
    
    // Loaded species definitions
    private val species = mutableMapOf<Int, TreeSpecies>()
    
    init {
        if (context != null) {
            loadTreeDefinitions(context)
        } else {
            loadDefaultSpecies()
        }
    }
    
    private fun loadTreeDefinitions(context: Context) {
        try {
            context.assets.open("world/trees.xml").use { inputStream ->
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val doc = builder.parse(inputStream)
                
                val treeElements = doc.getElementsByTagName("tree")
                for (i in 0 until treeElements.length) {
                    val element = treeElements.item(i) as Element
                    val treeSpecies = parseTreeElement(element)
                    if (treeSpecies != null) {
                        species[treeSpecies.speciesId] = treeSpecies
                    }
                }
                
                Log.i(TAG, "Loaded ${species.size} tree species definitions")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load tree definitions, using defaults", e)
            loadDefaultSpecies()
        }
    }
    
    private fun parseTreeElement(element: Element): TreeSpecies? {
        return try {
            TreeSpecies(
                speciesId = element.getAttribute("species_id").toIntOrNull() ?: return null,
                name = element.getAttribute("name"),
                textureId = parseUUID(element.getAttribute("texture_id")),
                droop = element.getAttribute("droop").toFloatOrNull() ?: 0f,
                twist = element.getAttribute("twist").toFloatOrNull() ?: 0f,
                branches = element.getAttribute("branches").toFloatOrNull() ?: 3f,
                depth = element.getAttribute("depth").toIntOrNull() ?: 1,
                scaleStep = element.getAttribute("scale_step").toFloatOrNull() ?: 0.5f,
                trunkDepth = element.getAttribute("trunk_depth").toIntOrNull() ?: 0,
                branchLength = element.getAttribute("branch_length").toFloatOrNull() ?: 1f,
                trunkLength = element.getAttribute("trunk_length").toFloatOrNull() ?: 1f,
                leafScale = element.getAttribute("leaf_scale").toFloatOrNull() ?: 1f,
                billboardScale = element.getAttribute("billboard_scale").toFloatOrNull() ?: 1f,
                billboardRatio = element.getAttribute("billboard_ratio").toFloatOrNull() ?: 1f,
                trunkAspect = element.getAttribute("trunk_aspect").toFloatOrNull() ?: 0.1f,
                branchAspect = element.getAttribute("branch_aspect").toFloatOrNull() ?: 0.05f,
                leafRotate = element.getAttribute("leaf_rotate").toFloatOrNull() ?: 0f,
                noiseMag = element.getAttribute("noise_mag").toFloatOrNull() ?: 0f,
                noiseScale = element.getAttribute("noise_scale").toFloatOrNull() ?: 1f,
                taper = element.getAttribute("taper").toFloatOrNull() ?: 0.5f,
                repeatZ = element.getAttribute("repeat_z").toIntOrNull() ?: 1
            )
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse tree element", e)
            null
        }
    }
    
    private fun parseUUID(uuidString: String): UUID {
        return try {
            UUID.fromString(uuidString)
        } catch (e: Exception) {
            UUID(0, 0) // Default null UUID
        }
    }
    
    private fun loadDefaultSpecies() {
        // Add basic default species for fallback
        species[SPECIES_PINE_1] = TreeSpecies(
            speciesId = SPECIES_PINE_1,
            name = "Pine 1",
            textureId = parseUUID("0187babf-6c0d-5891-ebed-4ecab1426683"),
            droop = 60f, twist = 5f, branches = 5f, depth = 1, scaleStep = 0.7f,
            trunkDepth = 6, branchLength = 8f, trunkLength = 11.5f, leafScale = 22f,
            billboardScale = 39.5f, billboardRatio = 1.1f, trunkAspect = 0.1f,
            branchAspect = 0.05f, leafRotate = 20f, noiseMag = 0.5f, noiseScale = 2.5f,
            taper = 0.8f, repeatZ = 3
        )
        
        species[SPECIES_OAK] = TreeSpecies(
            speciesId = SPECIES_OAK,
            name = "Oak",
            textureId = parseUUID("8a515889-eac9-fb55-8eba-d2dc09eb32c8"),
            droop = 35f, twist = 3f, branches = 4f, depth = 3, scaleStep = 0.7f,
            trunkDepth = 0, branchLength = 3f, trunkLength = 3.8f, leafScale = 7f,
            billboardScale = 10.25f, billboardRatio = 1f, trunkAspect = 0.15f,
            branchAspect = 0.07f, leafRotate = 0f, noiseMag = 1.2f, noiseScale = 4f,
            taper = 0.3f, repeatZ = 4
        )
        
        species[SPECIES_PALM_1] = TreeSpecies(
            speciesId = SPECIES_PALM_1,
            name = "Palm 1",
            textureId = parseUUID("ca4e8c27-473c-eb1c-2f5d-50ee3f07d85c"),
            droop = 0f, twist = 0f, branches = 3f, depth = 1, scaleStep = 0.5f,
            trunkDepth = 0, branchLength = 0.7f, trunkLength = 9f, leafScale = 10f,
            billboardScale = 13.25f, billboardRatio = 1f, trunkAspect = 0.035f,
            branchAspect = 0.03f, leafRotate = 0f, noiseMag = 0.2f, noiseScale = 6f,
            taper = 0.7f, repeatZ = 10
        )
        
        Log.i(TAG, "Loaded ${species.size} default tree species")
    }
    
    /**
     * Get species definition by ID
     */
    fun getSpecies(speciesId: Int): TreeSpecies? = species[speciesId]
    
    /**
     * Get all loaded species
     */
    fun getAllSpecies(): Collection<TreeSpecies> = species.values
    
    /**
     * Get species by name (case insensitive)
     */
    fun getSpeciesByName(name: String): TreeSpecies? {
        return species.values.find { it.name.equals(name, ignoreCase = true) }
    }
    
    /**
     * Get aquatic plant species
     */
    fun getAquaticSpecies(): List<TreeSpecies> = species.values.filter { it.isAquatic }
    
    /**
     * Get winter tree species
     */
    fun getWinterSpecies(): List<TreeSpecies> = species.values.filter { it.isWinter }
    
    /**
     * Get ground cover species
     */
    fun getGroundCoverSpecies(): List<TreeSpecies> = species.values.filter { it.isGroundCover }
    
    /**
     * Calculate appropriate LOD level for a tree at given distance
     */
    fun calculateLODLevel(species: TreeSpecies, distance: Float): Int {
        val billboardSwitchDistance = species.billboardScale * 10f
        
        return when {
            distance < billboardSwitchDistance * 0.25f -> 0  // Full detail
            distance < billboardSwitchDistance * 0.5f -> 1   // Medium detail
            distance < billboardSwitchDistance -> 2          // Low detail
            else -> 3                                        // Billboard
        }
    }
}
