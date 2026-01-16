# UI/UX Pattern Standards for Second Life Mobile Viewers

## Overview

This document establishes comprehensive standards for implementing user interfaces in Second Life mobile viewers, derived from analysis of the official Second Life Android viewer, Lumiya mobile viewer, and Material Design 3 guidelines.

## Design Philosophy

### Core Principles

1. **Mobile-First Design** - Optimize for touch interactions and limited screen space
2. **Consistency** - Maintain consistent UI patterns throughout the application
3. **Accessibility** - Ensure interfaces are usable by all users
4. **Performance** - Smooth animations and responsive interactions
5. **Platform Conformance** - Follow Android design guidelines

### Design System

```kotlin>
/**
 * Standard design system configuration
 */
object DesignSystem {
    
    // Color palette (Material 3)
    object Colors {
        const val PRIMARY = 0xFF6750A4.toInt()
        const val ON_PRIMARY = 0xFFFFFFFF.toInt()
        const val PRIMARY_CONTAINER = 0xFFEADDFF.toInt()
        const val ON_PRIMARY_CONTAINER = 0xFF21005D.toInt()
        const val SECONDARY = 0xFF625B71.toInt()
        const val ON_SECONDARY = 0xFFFFFFFF.toInt()
        const val TERTIARY = 0xFF7D5260.toInt()
        const val ON_TERTIARY = 0xFFFFFFFF.toInt()
        const val ERROR = 0xFFB3261E.toInt()
        const val ON_ERROR = 0xFFFFFFFF.toInt()
        const val BACKGROUND = 0xFFFFFBFE.toInt()
        const val ON_BACKGROUND = 0xFF1C1B1F.toInt()
        const val SURFACE = 0xFFFFFBFE.toInt()
        const val ON_SURFACE = 0xFF1C1B1F.toInt()
        const val SURFACE_VARIANT = 0xFFE7E0EC.toInt()
        const val ON_SURFACE_VARIANT = 0xFF49454F.toInt()
        const val OUTLINE = 0xFF79747E.toInt()
        const val OUTLINE_VARIANT = 0xFFCAC4D0.toInt()
        const val INVERSE_SURFACE = 0xFF313033.toInt()
        const val INVERSE_ON_SURFACE = 0xFFF4EFF4.toInt()
        const val INVERSE_PRIMARY = 0xFFEADDFF.toInt()
    }
    
    // Typography
    object Typography {
        const val DISPLAY_LARGE = 57
        const val DISPLAY_MEDIUM = 45
        const val DISPLAY_SMALL = 36
        const val HEADLINE_LARGE = 32
        const val HEADLINE_MEDIUM = 28
        const val HEADLINE_SMALL = 24
        const val TITLE_LARGE = 22
        const val TITLE_MEDIUM = 16
        const val TITLE_SMALL = 14
        const val LABEL_LARGE = 14
        const val LABEL_MEDIUM = 12
        const val LABEL_SMALL = 11
        const val BODY_LARGE = 16
        const val BODY_MEDIUM = 14
        const val BODY_SMALL = 12
    }
    
    // Spacing
    object Spacing {
        const val EXTRA_SMALL = 4.dp
        const val SMALL = 8.dp
        const val MEDIUM = 16.dp
        const val LARGE = 24.dp
        const val EXTRA_LARGE = 32.dp
        const val HUGE = 48.dp
    }
    
    // Border radius
    object Radius {
        const val SMALL = 4.dp
        const val MEDIUM = 8.dp
        const val LARGE = 16.dp
        const val EXTRA_LARGE = 24.dp
        const val CIRCLE = 999.dp
    }
    
    // Elevation
    object Elevation {
        const val LEVEL_0 = 0.dp
        const val LEVEL_1 = 1.dp
        const val LEVEL_2 = 3.dp
        const val LEVEL_3 = 6.dp
        const val LEVEL_4 = 8.dp
        const val LEVEL_5 = 12.dp
    }
}
```

## Navigation Patterns

### Standard Navigation Structure

```kotlin>
/**
 * Standard navigation destinations
 */
sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object World : Screen("world", "World")
    object Chat : Screen("chat", "Chat")
    object Inventory : Screen("inventory", "Inventory")
    object Profile : Screen("profile", "Profile")
    object Friends : Screen("friends", "Friends")
    object Map : Screen("map", "Map")
    object Settings : Screen("settings", "Settings")
}

/**
 * Standard navigation controller
 */
class NavController(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {
    
    private val backStack = mutableListOf<Screen>()
    
    fun navigateTo(screen: Screen, addToBackStack: Boolean = true) {
        val fragment = when (screen) {
            is Screen.Home -> HomeFragment()
            is Screen.World -> WorldFragment()
            is Screen.Chat -> ChatFragment()
            is Screen.Inventory -> InventoryFragment()
            is Screen.Profile -> ProfileFragment()
            is Screen.Friends -> FriendsFragment()
            is Screen.Map -> MapFragment()
            is Screen.Settings -> SettingsFragment()
        }
        
        val transaction = fragmentManager.beginTransaction()
            .replace(containerId, fragment, screen.route)
        
        if (addToBackStack) {
            backStack.add(screen)
            transaction.addToBackStack(screen.route)
        }
        
        transaction.commit()
    }
    
    fun navigateBack(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeLast()
            fragmentManager.popBackStack()
            true
        } else {
            false
        }
    }
    
    fun getCurrentScreen(): Screen? {
        return backStack.lastOrNull()
    }
}
```

### Bottom Navigation Bar

```xml>
<!-- Standard bottom navigation bar layout -->
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottom_navigation"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom"
    app:menu="@menu/bottom_navigation_menu"
    app:labelVisibilityMode="selected"
    app:itemIconTint="@color/bottom_nav_color"
    app:itemTextColor="@color/bottom_nav_color"
    app:itemBackground="?attr/colorSurface"
    app:elevation="@dimen/elevation_level_3"/>
```

```kotlin>
/**
 * Standard bottom navigation setup
 */
fun setupBottomNavigation(
    navView: BottomNavigationView,
    navController: NavController
) {
    navView.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> navController.navigateTo(Screen.Home)
            R.id.nav_world -> navController.navigateTo(Screen.World)
            R.id.nav_chat -> navController.navigateTo(Screen.Chat)
            R.id.nav_inventory -> navController.navigateTo(Screen.Inventory)
            R.id.nav_profile -> navController.navigateTo(Screen.Profile)
        }
        true
    }
    
    // Update selected item based on current screen
    navController.addOnDestinationChangedListener { _, destination, _ ->
        val itemId = when (destination.id) {
            Screen.Home.route -> R.id.nav_home
            Screen.World.route -> R.id.nav_world
            Screen.Chat.route -> R.id.nav_chat
            Screen.Inventory.route -> R.id.nav_inventory
            Screen.Profile.route -> R.id.nav_profile
            else -> null
        }
        itemId?.let { navView.selectedItemId = it }
    }
}
```

## Component Standards

### Standard Card Layout

```xml>
<!-- Standard card component -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="@dimen/spacing_medium"
    app:cardElevation="@dimen/elevation_level_2"
    app:cardCornerRadius="@dimen/radius_medium"
    app:strokeWidth="0dp"
    app:cardBackgroundColor="?attr/colorSurface">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/spacing_medium">
        
        <!-- Card content -->
        <TextView
            android:id="@+id/card_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.Material3.TitleMedium"
            android:textColor="?attr/colorOnSurface"/>
            
        <TextView
            android:id="@+id/card_subtitle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_small"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
            android:textColor="?attr/colorOnSurfaceVariant"/>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Standard List Item

```xml>
<!-- Standard list item -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="@dimen/spacing_small"
    android:layout_marginVertical="@dimen/spacing_extra_small"
    app:cardElevation="0dp"
    app:cardCornerRadius="@dimen/radius_medium"
    app:strokeWidth="1dp"
    app:strokeColor="?attr/colorOutline"
    app:cardBackgroundColor="?attr/colorSurface">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="@dimen/spacing_medium"
        android:gravity="center_vertical">
        
        <!-- Icon -->
        <ImageView
            android:id="@+id/item_icon"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:layout_marginEnd="@dimen/spacing_medium"
            android:contentDescription="@string/item_icon"/>
            
        <!-- Text content -->
        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">
            
            <TextView
                android:id="@+id/item_title"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textAppearance="@style/TextAppearance.Material3.BodyLarge"
                android:textColor="?attr/colorOnSurface"/>
                
            <TextView
                android:id="@+id/item_subtitle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="@dimen/spacing_extra_small"
                android:textAppearance="@style/TextAppearance.Material3.BodySmall"
                android:textColor="?attr/colorOnSurfaceVariant"/>
        </LinearLayout>
        
        <!-- Action -->
        <com.google.android.material.button.MaterialButton
            android:id="@+id/item_action"
            style="@style/Widget.Material3.Button.TextButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/action"/>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Standard Dialog

```kotlin>
/**
 * Standard dialog builder
 */
object StandardDialog {
    
    fun createConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onCancel: () -> Unit = {}
    ): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.confirm) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
    }
    
    fun createInputDialog(
        context: Context,
        title: String,
        hint: String,
        initialValue: String = "",
        onConfirm: (String) -> Unit
    ): MaterialAlertDialogBuilder {
        val input = EditText(context).apply {
            setText(initialValue)
            hint = hint
        }
        
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(input)
            .setPositiveButton(R.string.confirm) { _, _ ->
                onConfirm(input.text.toString())
            }
            .setNegativeButton(R.string.cancel, null)
    }
    
    fun createProgressDialog(
        context: Context,
        message: String
    ): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setView(R.layout.dialog_progress)
            .setCancelable(false)
    }
}
```

### Standard SnackBar

```kotlin>
/**
 * Standard snackbar display
 */
object StandardSnackBar {
    
    fun show(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        action: Pair<String, () -> Unit>? = null
    ) {
        val snackbar = Snackbar.make(view, message, duration)
        
        action?.let { (text, callback) ->
            snackbar.setAction(text) { callback() }
            snackbar.setActionTextColor(
                ContextCompat.getColor(view.context, R.color.primary)
            )
        }
        
        snackbar.show()
    }
    
    fun showError(view: View, message: String) {
        show(
            view = view,
            message = message,
            duration = Snackbar.LENGTH_LONG,
            action = Pair("Retry") { /* Implement retry */ }
        )
    }
}
```

## Chat Interface Standards

### Chat Bubble Component

```xml>
<!-- Incoming chat bubble -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginStart="@dimen/spacing_large"
    android:layout_marginEnd="@dimen/spacing_huge"
    android:layout_marginVertical="@dimen/spacing_small"
    app:cardElevation="@dimen/elevation_level_1"
    app:cardCornerRadius="@dimen/radius_large"
    app:cardBackgroundColor="?attr/colorSurfaceVariant">
    
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/spacing_medium"
        android:minWidth="120dp">
        
        <!-- Sender name -->
        <TextView
            android:id="@+id/sender_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.Material3.LabelSmall"
            android:textColor="?attr/colorPrimary"
            android:textStyle="bold"/>
            
        <!-- Message text -->
        <TextView
            android:id="@+id/message_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_extra_small"
            android:textAppearance="@style/TextAppearance.Material3.BodyMedium"
            android:textColor="?attr/colorOnSurfaceVariant"/>
            
        <!-- Timestamp -->
        <TextView
            android:id="@+id/timestamp"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="@dimen/spacing_small"
            android:textAppearance="@style/TextAppearance.Material3.LabelSmall"
            android:textColor="?attr/colorOutline"/>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Chat Input Component

```xml>
<!-- Standard chat input -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="@dimen/spacing_small"
    android:background="?attr/colorSurface"
    android:elevation="@dimen/elevation_level_3">
    
    <!-- Input field -->
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:layout_marginEnd="@dimen/spacing_small"
        style="@style/Widget.Material3.TextInputLayout.OutlinedBox">
        
        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/chat_input"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/type_message"
            android:maxLines="4"
            android:inputType="textMultiLine|textCapSentences"/>
    </com.google.android.material.textfield.TextInputLayout>
    
    <!-- Send button -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/send_button"
        style="@style/Widget.Material3.Button.IconButton.Filled"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:icon="@drawable/ic_send"/>
</LinearLayout>
```

## Avatar Interface Standards

### Avatar Preview Component

```xml>
<!-- Standard avatar preview -->
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="@dimen/spacing_medium"
    app:cardElevation="@dimen/elevation_level_2"
    app:cardCornerRadius="@dimen/radius_large">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="@dimen/spacing_large">
        
        <!-- Avatar image -->
        <ImageView
            android:id="@+id/avatar_image"
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:layout_gravity="center_horizontal"
            android:contentDescription="@string/avatar_preview"/>
            
        <!-- Avatar name -->
        <TextView
            android:id="@+id/avatar_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_marginTop="@dimen/spacing_medium"
            android:textAppearance="@style/TextAppearance.Material3.HeadlineSmall"
            android:textColor="?attr/colorOnSurface"/>
            
        <!-- Action buttons -->
        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_marginTop="@dimen/spacing_large"
            android:orientation="horizontal">
            
            <com.google.android.material.button.MaterialButton
                android:id="@+id/edit_appearance"
                style="@style/Widget.Material3.Button.OutlinedButton"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginEnd="@dimen/spacing_small"
                android:text="@string/edit_appearance"/>
                
            <com.google.android.material.button.MaterialButton
                android:id="@+id/change_outfit"
                style="@style/Widget.Material3.Button.Filled"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/change_outfit"/>
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

## Touch Interaction Standards

### Gesture Handling

```kotlin>
/**
 * Standard gesture detector
 */
class StandardGestureDetector(
    context: Context,
    private val onSingleTap: () -> Unit = {},
    private val onDoubleTap: () -> Unit = {},
    private val onLongPress: () -> Unit = {},
    private val onSwipe: (direction: SwipeDirection) -> Unit = {}
) : GestureDetector.SimpleOnGestureListener() {
    
    enum class SwipeDirection {
        UP, DOWN, LEFT, RIGHT
    }
    
    private val gestureDetector = GestureDetector(context, this)
    private val swipeThreshold = 100.dp
    private val swipeVelocityThreshold = 100
    
    private var downX = 0f
    private var downY = 0f
    
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onSingleTap()
        return true
    }
    
    override fun onDoubleTap(e: MotionEvent): Boolean {
        onDoubleTap()
        return true
    }
    
    override fun onLongPress(e: MotionEvent) {
        onLongPress()
    }
    
    override fun onDown(e: MotionEvent): Boolean {
        downX = e.x
        downY = e.y
        return true
    }
    
    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val diffX = e2.x - downX
        val diffY = e2.y - downY
        
        if (Math.abs(diffX) > Math.abs(diffY)) {
            // Horizontal swipe
            if (Math.abs(diffX) > swipeThreshold && 
                Math.abs(velocityX) > swipeVelocityThreshold) {
                onSwipe(if (diffX > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT)
                return true
            }
        } else {
            // Vertical swipe
            if (Math.abs(diffY) > swipeThreshold && 
                Math.abs(velocityY) > swipeVelocityThreshold) {
                onSwipe(if (diffY > 0) SwipeDirection.DOWN else SwipeDirection.UP)
                return true
            }
        }
        
        return false
    }
    
    fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }
}
```

### Touch Feedback

```kotlin>
/**
 * Standard touch feedback implementation
 */
object TouchFeedback {
    
    fun applyRippleEffect(view: View) {
        val rippleColor = ContextCompat.getColor(
            view.context,
            R.color.ripple_color
        )
        val rippleDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            null,
            null
        )
        view.background = rippleDrawable
    }
    
    fun performHapticFeedback(
        view: View,
        type: Int = HapticFeedbackConstants.VIRTUAL_KEY
    ) {
        view.performHapticFeedback(type)
    }
}
```

## Accessibility Standards

### Content Descriptions

```kotlin>
/**
 * Standard accessibility utilities
 */
object Accessibility {
    
    fun setContentDescription(view: View, description: String) {
        view.contentDescription = description
    }
    
    fun announceForAccessibility(view: View, message: String) {
        view.announceForAccessibility(message)
    }
    
    fun setAccessibilityDelegate(
        view: View,
        delegate: View.AccessibilityDelegate
    ) {
        view.accessibilityDelegate = delegate
    }
}
```

## Conclusion

This document establishes comprehensive standards for implementing user interfaces in Second Life mobile viewers. Key takeaways:

1. **Material Design 3** - Follow modern Android design guidelines
2. **Touch-First Design** - Optimize all interactions for touch
3. **Consistent Patterns** - Use standardized components throughout
4. **Accessibility** - Ensure interfaces are usable by everyone
5. **Performance** - Smooth animations and responsive interactions

These standards ensure:
- Consistent user experience across the application
- Intuitive navigation and interaction
- Professional and polished appearance
- Optimal performance on mobile devices
- Accessibility for all users

---

**Document Version**: 1.0  
**Last Updated**: January 16, 2025  
**Status**: ✅ Complete