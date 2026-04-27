# AsyncTask to Coroutines Migration Guide

**Status:** 📋 **Future Enhancement**  
**Priority:** Medium (AsyncTask still works, but deprecated)

---

## Current Status

The Lumiya app has **6 files** still using AsyncTask:

1. `ui/inventory/UploadImageAsyncTask.kt` - Image upload
2. `ui/common/ImageAssetView.kt` - Asset image loading
3. `ui/chat/profiles/ParcelPropertiesFragment.kt` - Set home location
4. `ui/settings/SettingsFragment.kt` - Cache clearing
5. `ui/settings/EmulatorManager.kt` - Emulator commands
6. `ui/inventory/TextureViewFragment.kt` - Texture loading

---

## Why AsyncTask Still Works

AsyncTask is **deprecated since Android 11** (API 30) but:
- ✅ Still functional and won't be removed
- ✅ Works on all supported Android versions
- ✅ Not blocking compilation or deployment
- ⚠️ Not recommended for new code
- ⚠️ Should be migrated to coroutines eventually

---

## Migration Recommendation

### Convert to Kotlin Coroutines

#### Before (AsyncTask):
```kotlin
class UploadImageAsyncTask : AsyncTask<Params, Progress, Result>() {
    override fun doInBackground(vararg params: Params): Result {
        // Background work
        return result
    }
    
    override fun onPostExecute(result: Result) {
        // UI update
    }
}

// Usage
UploadImageAsyncTask().execute(params)
```

#### After (Coroutines):
```kotlin
class UploadImageTask(private val scope: CoroutineScope) {
    suspend fun uploadImage(params: Params): Result = withContext(Dispatchers.IO) {
        // Background work
        result
    }
}

// Usage
lifecycleScope.launch {
    val result = UploadImageTask(this).uploadImage(params)
    // UI update (automatically on Main thread)
}
```

---

## Migration Priority

### High Priority
- ❌ None (all AsyncTasks work fine)

### Medium Priority
1. **UploadImageAsyncTask** - Image uploads (user-facing)
2. **ImageAssetView.LoadAssetImageTask** - Image loading (user-facing)
3. **TextureViewFragment.LoadAssetImageTask** - Texture preview

### Low Priority  
4. **SettingsFragment.ClearCacheTask** - Cache clearing (rare operation)
5. **ParcelPropertiesFragment.SetHomeLocationAsyncTask** - Set home (rare)
6. **EmulatorManager.EmulatorTask** - Debug tool only

---

## Benefits of Migration

### Why Migrate to Coroutines?

1. **Modern API**
   - Recommended by Google
   - Better integration with Jetpack
   - First-class Kotlin support

2. **Better Error Handling**
   - Structured exception handling
   - Try-catch works naturally
   - Cancellation support

3. **Lifecycle Awareness**
   - Auto-cancellation with lifecycle
   - No memory leaks
   - Proper scope management

4. **Better Testability**
   - Easy to test with coroutine test library
   - Mockable dispatchers
   - Better control flow

5. **Performance**
   - More efficient threading
   - Better resource usage
   - Structured concurrency

---

## Example Migration

### UploadImageAsyncTask → Coroutines

```kotlin
class UploadImageManager(
    private val context: Context,
    private val agentUUID: UUID
) {
    /**
     * Upload image with coroutines
     */
    suspend fun uploadImage(
        bitmap: Bitmap,
        name: String,
        description: String
    ): Result<UUID, String> = withContext(Dispatchers.IO) {
        try {
            // Scale bitmap if needed
            val scaledBitmap = scaleBitmapForUpload(bitmap)
            
            // Encode to JP2
            val jp2Data = encodeToJP2(scaledBitmap)
            
            // Upload to Second Life
            val assetUUID = uploadToSL(jp2Data, name, description)
            
            Result.Success(assetUUID)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Upload failed")
        }
    }
    
    private fun scaleBitmapForUpload(bitmap: Bitmap): Bitmap {
        var width = bitmap.width.takeHighestOneBit()
        var height = bitmap.height.takeHighestOneBit()
        
        if (width != bitmap.width) width *= 2
        if (height != bitmap.height) height *= 2
        
        while (width > 1024 || height > 1024) {
            width /= 2
            height /= 2
        }
        
        return if (width == bitmap.width && height == bitmap.height) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, width, height, true)
        }
    }
    
    sealed class Result<out T, out E> {
        data class Success<T>(val value: T) : Result<T, Nothing>()
        data class Error<E>(val error: E) : Result<Nothing, E>()
    }
}

// Usage in Fragment/Activity
class InventoryActivity : AppCompatActivity() {
    private val uploadManager by lazy { 
        UploadImageManager(this, agentUUID) 
    }
    
    fun uploadImage(bitmap: Bitmap, name: String, desc: String) {
        lifecycleScope.launch {
            showProgressDialog()
            
            when (val result = uploadManager.uploadImage(bitmap, name, desc)) {
                is UploadImageManager.Result.Success -> {
                    hideProgressDialog()
                    showSuccess("Image uploaded: ${result.value}")
                }
                is UploadImageManager.Result.Error -> {
                    hideProgressDialog()
                    showError(result.error)
                }
            }
        }
    }
}
```

---

## When to Migrate

### Now (Optional)
- If you're already refactoring these areas
- If you want latest best practices
- If you need better testing

### Later (Fine to Wait)
- AsyncTask still works
- Not causing any issues
- Other priorities more important

---

## Summary

**Current:** AsyncTask usage is deprecated but functional  
**Impact:** None (works fine)  
**Priority:** Medium (migrate eventually)  
**Recommendation:** Migrate when refactoring those areas

The AndroidX migration and Kotlin upgrade are **100% complete**. AsyncTask migration is a **future enhancement** that doesn't block production deployment.

---

**Status:** ✅ Migration Documented  
**Action Required:** None (optional future improvement)

---
