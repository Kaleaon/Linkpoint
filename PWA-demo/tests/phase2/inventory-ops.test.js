const { InventoryCore } = require('../../js/phase2/inventory-core.js');
const { InventoryOperations } = require('../../js/phase2/inventory-ops.js');

async function runTests() {
  console.log('Running inventory-ops.test.js...');
  let testsPassed = 0;
  let testsFailed = 0;

  function assert(condition, message) {
    if (condition) {
      console.log(`✅ PASS: ${message}`);
      testsPassed++;
    } else {
      console.error(`❌ FAIL: ${message}`);
      testsFailed++;
    }
  }

  const core = new InventoryCore();
  const ops = new InventoryOperations(core);

  // Setup
  const root = core.createFolder('root-1', { name: 'Root Folder' });
  const subFolder = core.createFolder('folder-1', { name: 'Sub Folder', parentId: 'root-1' });
  const item1 = core.addItem('item-1', { name: 'Item 1', folderId: 'folder-1' });

  // Test 1: Delete item
  try {
    await ops.deleteItem('item-1', 'item');
    assert(core.getItem('item-1') === null, 'Item deleted from core items map');

    const folderContents = core.listFolderContents('folder-1');
    assert(folderContents.items.length === 0, 'Item deleted from folder items array');
  } catch (e) {
    assert(false, `Delete item threw error: ${e.message}`);
  }

  // Test 2: Delete folder
  try {
    await ops.deleteItem('folder-1', 'folder');
    assert(core.getFolder('folder-1') === null, 'Folder deleted from core folders map');

    const rootContents = core.listFolderContents('root-1');
    assert(rootContents.folders.length === 0, 'Folder deleted from parent children array');
  } catch (e) {
    assert(false, `Delete folder threw error: ${e.message}`);
  }

  // Summary
  console.log(`\nTest Summary: ${testsPassed} passed, ${testsFailed} failed`);
  if (testsFailed > 0) {
    process.exit(1);
  }
}

runTests();
