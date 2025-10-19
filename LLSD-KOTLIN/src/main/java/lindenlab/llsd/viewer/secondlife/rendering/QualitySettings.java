/*
 * Quality Settings - Overall quality control for rendering system
 */

package lindenlab.llsd.viewer.secondlife.rendering;

import java.util.HashMap;
import java.util.Map;

public class QualitySettings {
    private float overallQuality = 0.6f; // 0.0 to 1.0
    private boolean autoAdjustQuality = true;
    private float renderScale = 1.0f;
    private int maxDrawDistance = 256;
    
    public void setOverallQuality(float quality) {
        this.overallQuality = Math.max(0.0f, Math.min(1.0f, quality));
    }
    
    public float getOverallQuality() { return overallQuality; }
    
    public void setAutoAdjustQuality(boolean auto) { this.autoAdjustQuality = auto; }
    public boolean isAutoAdjustQuality() { return autoAdjustQuality; }
    
    public void setRenderScale(float scale) { this.renderScale = scale; }
    public float getRenderScale() { return renderScale; }
    
    public void setMaxDrawDistance(int distance) { this.maxDrawDistance = distance; }
    public int getMaxDrawDistance() { return maxDrawDistance; }
    
    public void applySetting(String setting, Object value) {
        switch (setting.toLowerCase()) {
            case "overallquality":
                if (value instanceof Number) {
                    setOverallQuality(((Number) value).floatValue());
                }
                break;
            case "autoadjust":
                if (value instanceof Boolean) {
                    setAutoAdjustQuality((Boolean) value);
                }
                break;
            case "renderscale":
                if (value instanceof Number) {
                    setRenderScale(((Number) value).floatValue());
                }
                break;
            case "maxdrawdistance":
                if (value instanceof Number) {
                    setMaxDrawDistance(((Number) value).intValue());
                }
                break;
        }
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("overallQuality", overallQuality);
        map.put("autoAdjustQuality", autoAdjustQuality);
        map.put("renderScale", renderScale);
        map.put("maxDrawDistance", maxDrawDistance);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("overallQuality")) {
            setOverallQuality(((Number) map.get("overallQuality")).floatValue());
        }
        if (map.containsKey("autoAdjustQuality")) {
            setAutoAdjustQuality((Boolean) map.get("autoAdjustQuality"));
        }
        if (map.containsKey("renderScale")) {
            setRenderScale(((Number) map.get("renderScale")).floatValue());
        }
        if (map.containsKey("maxDrawDistance")) {
            setMaxDrawDistance(((Number) map.get("maxDrawDistance")).intValue());
        }
    }
}