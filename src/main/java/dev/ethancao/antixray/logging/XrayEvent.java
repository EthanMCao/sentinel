package dev.ethancao.antixray.logging;

import org.bukkit.Location;

/**
 * Represents a suspicious xray mining event for logging.
 * This structure matches the SIEM-compatible schema for Wazuh/ELK ingestion.
 */
public class XrayEvent {
    private final String ts;                // Timestamp in ISO 8601 format
    private final String player;            // Player name
    private final String player_uuid;       // Player UUID
    private final String event_type;        // Type of event (e.g., "diamond_mine")
    private final String ore;               // Ore type (e.g., "DIAMOND_ORE")
    private final LocationData location;    // Mining location
    private final int stone_mined;          // Total stone blocks mined
    private final int diamond_mined;        // Total diamonds mined
    private final int total_ores_mined;     // Total ores of all types
    private final double diamond_ratio;     // Diamond to stone ratio
    private final int suspicion_score;      // Current suspicion score
    private final String detection_type;    // Which detection triggered
    private final String reason;            // Human-readable reason
    
    /**
     * Inner class for location data.
     */
    public static class LocationData {
        private final int x;
        private final int y;
        private final int z;
        private final String world;
        
        public LocationData(int x, int y, int z, String world) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.world = world;
        }
    }
    
    public XrayEvent(String timestamp, String playerName, String playerUuid, String eventType,
                     String oreType, Location location, int stoneMined, int diamondMined,
                     int totalOres, double diamondRatio, int suspicionScore,
                     String detectionType, String reason) {
        this.ts = timestamp;
        this.player = playerName;
        this.player_uuid = playerUuid;
        this.event_type = eventType;
        this.ore = oreType;
        this.location = new LocationData(
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            location.getWorld().getName()
        );
        this.stone_mined = stoneMined;
        this.diamond_mined = diamondMined;
        this.total_ores_mined = totalOres;
        this.diamond_ratio = diamondRatio;
        this.suspicion_score = suspicionScore;
        this.detection_type = detectionType;
        this.reason = reason;
    }
}

