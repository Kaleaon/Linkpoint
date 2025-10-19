# LLSD Java Automation Scripts

This directory contains automation scripts for maintaining the LLSD Java library.

## Available Scripts

### resolve-conflicts.sh

**Purpose**: Automatically resolve merge conflicts with the master branch  
**Usage**: `./scripts/resolve-conflicts.sh`

This script intelligently resolves merge conflicts by:

1. **Preserving Superior Implementations**: Always keeps master's comprehensive serializer implementations
2. **Combining Configuration Files**: Merges .gitignore files to include both sets of rules  
3. **Removing Incompatible Code**: Cleans up factory classes that conflict with master's API
4. **Verifying Results**: Compiles and tests the code after resolution

**Conflict Resolution Strategy**:
- `.gitignore`: Combined approach (merge both versions)
- Serializers (`LLSDBinarySerializer.java`, `LLSDNotationSerializer.java`, etc.): Use master versions
- Tests: Use master's comprehensive test suites
- Incompatible factory classes: Remove entirely
- Demo files: Clean up and replace with compatible versions

**When to Use**: 
- During merge conflicts with the master branch
- When updating feature branches with master changes
- For automated CI/CD conflict resolution

**Example**:
```bash
# Create merge conflict
git fetch origin master
git merge master --no-commit

# Resolve automatically  
./scripts/resolve-conflicts.sh

# Complete the merge
git commit
```

**Output**: The script provides detailed progress information and verifies the resolution by compilation and testing.

## Adding New Scripts

When adding new automation scripts:

1. Make them executable: `chmod +x scripts/your-script.sh`
2. Add appropriate error handling with `set -e`
3. Provide clear progress output with emoji indicators
4. Verify results before completing operations
5. Update this README with documentation

## Requirements

- Bash shell
- Git
- Maven (for compilation verification)
- Access to the repository's master branch