package com.linkpoint.pwa;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Notifications Plugin for Linkpoint PWA
 * Provides advanced notification features including channels, vibration patterns, and custom sounds
 */
@CapacitorPlugin(name = "EnhancedNotifications")
public class EnhancedNotificationsPlugin extends Plugin {

    private static final String CHANNEL_ID_DEFAULT = "linkpoint_default";
    private static final String CHANNEL_ID_MESSAGES = "linkpoint_messages";
    private static final String CHANNEL_ID_FRIENDS = "linkpoint_friends";
    private static final String CHANNEL_ID_GROUPS = "linkpoint_groups";
    private static final String CHANNEL_ID_INVENTORY = "linkpoint_inventory";

    private NotificationManager notificationManager;
    private Vibrator vibrator;
    private int notificationIdCounter = 1000;

    @Override
    public void load() {
        super.load();
        notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        createNotificationChannels();
    }

    /**
     * Create notification channels for Android O+
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<NotificationChannel> channels = new ArrayList<>();

            // Default channel
            NotificationChannel defaultChannel = new NotificationChannel(
                CHANNEL_ID_DEFAULT,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            defaultChannel.setDescription("General app notifications");
            defaultChannel.enableLights(true);
            defaultChannel.setLightColor(Color.BLUE);
            defaultChannel.enableVibration(true);
            channels.add(defaultChannel);

            // Messages channel
            NotificationChannel messagesChannel = new NotificationChannel(
                CHANNEL_ID_MESSAGES,
                "Instant Messages",
                NotificationManager.IMPORTANCE_HIGH
            );
            messagesChannel.setDescription("Instant messages and chat notifications");
            messagesChannel.enableLights(true);
            messagesChannel.setLightColor(Color.GREEN);
            messagesChannel.enableVibration(true);
            messagesChannel.setVibrationPattern(new long[]{0, 250, 250, 250});
            channels.add(messagesChannel);

            // Friends channel
            NotificationChannel friendsChannel = new NotificationChannel(
                CHANNEL_ID_FRIENDS,
                "Friend Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            friendsChannel.setDescription("Friend online/offline notifications");
            friendsChannel.enableLights(true);
            friendsChannel.setLightColor(Color.CYAN);
            channels.add(friendsChannel);

            // Groups channel
            NotificationChannel groupsChannel = new NotificationChannel(
                CHANNEL_ID_GROUPS,
                "Group Notices",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            groupsChannel.setDescription("Group announcements and notices");
            groupsChannel.enableLights(true);
            groupsChannel.setLightColor(Color.YELLOW);
            channels.add(groupsChannel);

            // Inventory channel
            NotificationChannel inventoryChannel = new NotificationChannel(
                CHANNEL_ID_INVENTORY,
                "Inventory Offers",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            inventoryChannel.setDescription("Inventory transfer notifications");
            channels.add(inventoryChannel);

            notificationManager.createNotificationChannels(channels);
        }
    }

    /**
     * Show a notification with advanced options
     */
    @PluginMethod
    public void show(PluginCall call) {
        String title = call.getString("title", "Linkpoint");
        String body = call.getString("body", "");
        String channelType = call.getString("channelType", "default");
        String priority = call.getString("priority", "default");
        boolean autoCancel = call.getBoolean("autoCancel", true);
        boolean vibrate = call.getBoolean("vibrate", true);
        boolean sound = call.getBoolean("sound", true);
        String actionUrl = call.getString("actionUrl", "");
        
        String channelId = getChannelId(channelType);
        int notificationId = notificationIdCounter++;

        // Create intent for notification tap
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (!actionUrl.isEmpty()) {
            intent.putExtra("actionUrl", actionUrl);
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            getContext(),
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
            .setSmallIcon(getContext().getApplicationInfo().icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(getPriority(priority))
            .setContentIntent(pendingIntent)
            .setAutoCancel(autoCancel);

        // Add style for long text
        if (body.length() > 40) {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        }

        // Add sound
        if (sound) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(soundUri);
        }

        // Add vibration
        if (vibrate && vibrator != null && vibrator.hasVibrator()) {
            builder.setVibrate(getVibrationPattern(channelType));
        }

        // Add color
        builder.setColor(getNotificationColor(channelType));

        // Show notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.notify(notificationId, builder.build());

        // Return notification ID
        JSObject result = new JSObject();
        result.put("notificationId", notificationId);
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Cancel a notification by ID
     */
    @PluginMethod
    public void cancel(PluginCall call) {
        Integer notificationId = call.getInt("notificationId");
        if (notificationId == null) {
            call.reject("Missing notificationId");
            return;
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.cancel(notificationId);

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Cancel all notifications
     */
    @PluginMethod
    public void cancelAll(PluginCall call) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.cancelAll();

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Trigger vibration with custom pattern
     */
    @PluginMethod
    public void vibrate(PluginCall call) {
        if (vibrator == null || !vibrator.hasVibrator()) {
            call.reject("Vibrator not available");
            return;
        }

        String pattern = call.getString("pattern", "default");
        long[] vibrationPattern = getVibrationPatternByName(pattern);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createWaveform(vibrationPattern, -1);
            vibrator.vibrate(effect);
        } else {
            vibrator.vibrate(vibrationPattern, -1);
        }

        JSObject result = new JSObject();
        result.put("success", true);
        call.resolve(result);
    }

    /**
     * Check notification permission status
     */
    @PluginMethod
    public void checkPermission(PluginCall call) {
        boolean enabled = NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
        
        JSObject result = new JSObject();
        result.put("granted", enabled);
        call.resolve(result);
    }

    /**
     * Request notification permission
     */
    @PluginMethod
    public void requestPermission(PluginCall call) {
        // On Android, notifications are enabled by default
        // User must manually disable in settings
        JSObject result = new JSObject();
        result.put("granted", true);
        call.resolve(result);
    }

    // Helper methods

    private String getChannelId(String channelType) {
        switch (channelType.toLowerCase()) {
            case "messages":
            case "im":
            case "chat":
                return CHANNEL_ID_MESSAGES;
            case "friends":
                return CHANNEL_ID_FRIENDS;
            case "groups":
            case "group_notice":
                return CHANNEL_ID_GROUPS;
            case "inventory":
                return CHANNEL_ID_INVENTORY;
            default:
                return CHANNEL_ID_DEFAULT;
        }
    }

    private int getPriority(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
            case "max":
                return NotificationCompat.PRIORITY_HIGH;
            case "low":
                return NotificationCompat.PRIORITY_LOW;
            case "min":
                return NotificationCompat.PRIORITY_MIN;
            default:
                return NotificationCompat.PRIORITY_DEFAULT;
        }
    }

    private int getNotificationColor(String channelType) {
        switch (channelType.toLowerCase()) {
            case "messages":
                return Color.parseColor("#4CAF50"); // Green
            case "friends":
                return Color.parseColor("#00BCD4"); // Cyan
            case "groups":
                return Color.parseColor("#FFC107"); // Amber
            case "inventory":
                return Color.parseColor("#9C27B0"); // Purple
            default:
                return Color.parseColor("#2196F3"); // Blue
        }
    }

    private long[] getVibrationPattern(String channelType) {
        switch (channelType.toLowerCase()) {
            case "messages":
                return new long[]{0, 250, 100, 250}; // Double buzz
            case "friends":
                return new long[]{0, 200}; // Single short
            case "groups":
                return new long[]{0, 100, 100, 100, 100, 100}; // Triple short
            default:
                return new long[]{0, 300}; // Single medium
        }
    }

    private long[] getVibrationPatternByName(String pattern) {
        switch (pattern.toLowerCase()) {
            case "short":
                return new long[]{0, 100};
            case "medium":
                return new long[]{0, 300};
            case "long":
                return new long[]{0, 500};
            case "double":
                return new long[]{0, 200, 100, 200};
            case "triple":
                return new long[]{0, 150, 100, 150, 100, 150};
            case "sos":
                return new long[]{0, 100, 100, 100, 100, 100, 300, 300, 300, 100, 100, 100};
            default:
                return new long[]{0, 250};
        }
    }
}
