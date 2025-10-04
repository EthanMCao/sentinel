package dev.ethancao.antixray.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages plugin configuration and provides easy access to config values.
 */
public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    /**
     * Load or reload the configuration file.
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    // === Pure Ore Mining Detection ===
    
    public boolean isPureOreMiningEnabled() {
        return config.getBoolean("detection.pure-ore-mining.enabled", true);
    }
    
    public int getPureOreMiningValuableOresThreshold() {
        return config.getInt("detection.pure-ore-mining.valuable-ores-threshold", 5);
    }
    
    public int getPureOreMiningStoneThreshold() {
        return config.getInt("detection.pure-ore-mining.stone-threshold", 20);
    }
    
    public int getPureOreMiningSuspicionPoints() {
        return config.getInt("detection.pure-ore-mining.suspicion-points", 20);
    }
    
    // === Low Stone Mining Detection ===
    
    public boolean isLowStoneMiningEnabled() {
        return config.getBoolean("detection.low-stone-mining.enabled", true);
    }
    
    public int getLowStoneMiningValuableOresThreshold() {
        return config.getInt("detection.low-stone-mining.valuable-ores-threshold", 8);
    }
    
    public int getLowStoneMiningStoneThreshold() {
        return config.getInt("detection.low-stone-mining.stone-threshold", 50);
    }
    
    public int getLowStoneMiningSuspicionPoints() {
        return config.getInt("detection.low-stone-mining.suspicion-points", 15);
    }
    
    // === High Ratio Detection ===
    
    public boolean isHighRatioEnabled() {
        return config.getBoolean("detection.high-ratio.enabled", true);
    }
    
    public int getHighRatioStoneBaseline() {
        return config.getInt("detection.high-ratio.stone-baseline", 50);
    }
    
    public double getHighRatioDiamondVeryHigh() {
        return config.getDouble("detection.high-ratio.diamond-ratio-very-high", 0.05);
    }
    
    public double getHighRatioDiamondHigh() {
        return config.getDouble("detection.high-ratio.diamond-ratio-high", 0.02);
    }
    
    public int getHighRatioSuspicionPointsVeryHigh() {
        return config.getInt("detection.high-ratio.suspicion-points-very-high", 10);
    }
    
    public int getHighRatioSuspicionPointsHigh() {
        return config.getInt("detection.high-ratio.suspicion-points-high", 5);
    }
    
    // === Logging Settings ===
    
    public boolean isConsoleLoggingEnabled() {
        return config.getBoolean("logging.console.enabled", true);
    }
    
    public boolean isJsonLoggingEnabled() {
        return config.getBoolean("logging.json-file.enabled", true);
    }
    
    public String getJsonLogFilePath() {
        return config.getString("logging.json-file.file-path", "logs/antixray.jsonl");
    }
    
    // === Valuable Ores ===
    
    public boolean isDiamondValuable() {
        return config.getBoolean("valuable-ores.diamond", true);
    }
    
    public boolean isEmeraldValuable() {
        return config.getBoolean("valuable-ores.emerald", true);
    }
    
    public boolean isGoldValuable() {
        return config.getBoolean("valuable-ores.gold", true);
    }
    
    // === Manual Flags ===
    
    public int getManualFlagSuspicionPoints() {
        return config.getInt("manual-flags.suspicion-points-per-flag", 15);
    }
}
