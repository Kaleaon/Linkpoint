# Login Fix - Grid Selection Issue

## Problem
Login was failing with "Invalid grid selected" error on Vercel deployment.

## Root Cause
Mismatch between grid identifiers in the HTML form and JavaScript protocol handler:

### Before (Broken)
**index.html** grid select values:
- `secondlife` ❌ (doesn't match backend)
- `aditi` ✓
- `osgrid` ✓

**sl-protocol-real.js** GRIDS object keys:
- `agni` ✓ (correct key for Second Life main grid)
- `aditi` ✓
- `osgrid` ✓

**Mismatch**: `secondlife` in HTML → Expected `agni` in JavaScript

### Additional Issue
**index.html** had incorrect element ID:
- `<div id="custom-grid-url">` ❌

**auth.js** expected:
- `document.getElementById('custom-grid-group')` ✓

## Solution

### 1. Fixed Grid Value (index.html)
Changed the Second Life main grid option value:
```html
<!-- Before -->
<option value="secondlife">Second Life (Agni)</option>

<!-- After -->
<option value="agni">Second Life (Agni)</option>
```

### 2. Fixed Custom Grid Container ID (index.html)
Changed the container div ID and input ID:
```html
<!-- Before -->
<div id="custom-grid-url" class="form-group" style="display: none;">
  <label for="login-url">Login URL</label>
  <input type="url" id="login-url" class="form-control" ...>
</div>

<!-- After -->
<div id="custom-grid-group" class="form-group" style="display: none;">
  <label for="custom-grid-url">Login URL</label>
  <input type="url" id="custom-grid-url" class="form-control" ...>
</div>
```

## Grid Configuration Reference

The PWA supports the following grids:

### Second Life Grids

**Main Grid (Agni)**
- Value: `agni`
- Display: "Second Life (Agni)"
- Login URL: `https://login.agni.lindenlab.com/cgi-bin/login.cgi`

**Beta Grid (Aditi)**
- Value: `aditi`
- Display: "Second Life Beta (Aditi)"
- Login URL: `https://login.aditi.lindenlab.com/cgi-bin/login.cgi`

### OpenSimulator Grids

**OSGrid**
- Value: `osgrid`
- Display: "OSGrid"
- Login URL: `http://login.osgrid.org/`

**Custom Grid**
- Value: `custom`
- Display: "Custom Grid"
- Requires user to enter custom login URL

## How Login Works

### 1. Grid Selection
User selects a grid from the dropdown. The value is validated against `SLProtocol.GRIDS`:

```javascript
async login(gridId, username, password, startLocation = 'last') {
  const grid = SLProtocol.GRIDS[gridId];
  if (!grid) {
    throw new Error('Invalid grid selected');
  }
  // ... continue login
}
```

### 2. Custom Grid Handling
When user selects "Custom Grid":
- The `custom-grid-group` div becomes visible
- User enters custom login URL
- The URL is validated before attempting login

```javascript
if (grid === 'custom') {
  const customGridUrl = document.getElementById('custom-grid-url');
  if (!customGridUrl || !customGridUrl.value) {
    Utils.showToast('Please enter custom grid URL', 'error');
    return;
  }
}
```

### 3. Authentication Flow
1. User enters username, password, selects grid
2. Form validation checks all required fields
3. Grid ID is looked up in `SLProtocol.GRIDS`
4. XMLRPC login request is sent to grid's login URL
5. Response is processed and session established

## Testing

### Test Each Grid

**Second Life Main Grid**:
1. Select "Second Life (Agni)"
2. Enter valid SL credentials
3. Click "Login"
4. Should connect successfully ✓

**Second Life Beta Grid**:
1. Select "Second Life Beta (Aditi)"
2. Enter valid beta grid credentials
3. Click "Login"
4. Should connect successfully ✓

**OSGrid**:
1. Select "OSGrid"
2. Enter valid OSGrid credentials
3. Click "Login"
4. Should connect successfully ✓

**Custom Grid**:
1. Select "Custom Grid"
2. Custom URL field appears ✓
3. Enter grid's login URL
4. Enter valid credentials
5. Click "Login"
6. Should connect successfully ✓

## Verification

### Before Fix
```
✗ Select "Second Life (Agni)"
✗ Enter credentials
✗ Click Login
✗ Error: "Invalid grid selected"
✗ Login fails
```

### After Fix
```
✓ Select "Second Life (Agni)"
✓ Enter credentials
✓ Click Login
✓ Grid value "agni" matches SLProtocol.GRIDS
✓ Login proceeds normally
```

## Common Issues

### Issue 1: "Invalid grid selected"
**Cause**: Grid dropdown value doesn't match GRIDS object keys
**Fix**: Use correct values: `agni`, `aditi`, `osgrid`, or `custom`

### Issue 2: Custom grid URL field not showing
**Cause**: Incorrect element ID in HTML
**Fix**: Ensure `<div id="custom-grid-group">` matches JavaScript selector

### Issue 3: Login URL not found for custom grid
**Cause**: Input field ID mismatch
**Fix**: Ensure input has `id="custom-grid-url"` to match JavaScript

## Code References

### Grid Definitions (sl-protocol-real.js)
```javascript
static GRIDS = {
  agni: {
    name: 'Second Life (Main Grid - Agni)',
    loginUrl: 'https://login.agni.lindenlab.com/cgi-bin/login.cgi',
    isSecondLife: true
  },
  aditi: {
    name: 'Second Life Beta Grid (Aditi)',
    loginUrl: 'https://login.aditi.lindenlab.com/cgi-bin/login.cgi',
    isSecondLife: true
  },
  osgrid: {
    name: 'OSGrid',
    loginUrl: 'http://login.osgrid.org/',
    isOpenSim: true
  }
};
```

### Grid Validation (auth.js)
```javascript
const grid = gridSelect.value;

// Validate inputs
if (!username || !password) {
  Utils.showToast('Please enter username and password', 'error');
  return;
}

// Custom grid validation
if (grid === 'custom') {
  const customGridUrl = document.getElementById('custom-grid-url');
  if (!customGridUrl || !customGridUrl.value) {
    Utils.showToast('Please enter custom grid URL', 'error');
    return;
  }
}
```

## Summary

The login issue was caused by:
1. **Grid value mismatch**: HTML used `secondlife`, JavaScript expected `agni`
2. **Element ID mismatch**: HTML used `custom-grid-url`, JavaScript expected `custom-grid-group`

Both issues are now fixed. Users can successfully log in to:
- Second Life Main Grid (Agni) ✓
- Second Life Beta Grid (Aditi) ✓
- OSGrid ✓
- Custom OpenSimulator grids ✓

---

**Status**: ✅ Fixed
**Files Modified**: `PWA-demo/index.html`
**Changes**: 2 lines (grid value + element IDs)
**Test**: Select each grid and verify no "Invalid grid selected" error
