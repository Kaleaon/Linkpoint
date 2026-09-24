#!/bin/bash
# Linkpoint Branch Cleanup Script
# Deletes all remote branches except 'main' and the cleanup PR branch
#
# Usage: ./cleanup-branches.sh
# Run this after the consolidated PR has been merged.

set -e

echo "=== Linkpoint Branch Cleanup ==="
echo "This will delete ALL remote branches except 'main'."
echo ""

# Confirm
read -p "Are you sure? (y/N) " confirm
if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
    echo "Aborted."
    exit 0
fi

echo ""
echo "Fetching all branches..."
git fetch --all --prune

# Get all remote branches except main and HEAD
branches=$(git branch -r | sed 's/^ *//' | grep -v HEAD | grep -v 'origin/main$' | sed 's|origin/||')
total=$(echo "$branches" | wc -l)
count=0
failed=0

echo "Found $total branches to delete."
echo ""

while IFS= read -r branch; do
    [ -z "$branch" ] && continue
    count=$((count + 1))
    echo "[$count/$total] Deleting: $branch"
    if git push origin --delete "$branch" 2>/dev/null; then
        echo "  Deleted."
    else
        echo "  FAILED to delete (may already be gone)."
        failed=$((failed + 1))
    fi
done <<< "$branches"

echo ""
echo "=== Done ==="
echo "Deleted: $((count - failed)) branches"
echo "Failed:  $failed branches"
echo ""
echo "Remaining branches:"
git branch -r
