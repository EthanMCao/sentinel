package dev.ethancao.antixray.tracking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages mining statistics for all players.
 * Stores data in memory (will be lost on server restart).
 */
public class PlayerDataManager {
    private final Map<UUID, MiningStats> playerStats;
    
    public PlayerDataManager() {
        this.playerStats = new HashMap<>();
    }
    
    /**
     * Get or create mining stats for a player.
     */
    public MiningStats getOrCreateStats(UUID playerUuid, String playerName) {
        return playerStats.computeIfAbsent(playerUuid, uuid -> new MiningStats(uuid, playerName));
    }
    
    /**
     * Get existing stats (returns null if player has no data).
     */
    public MiningStats getStats(UUID playerUuid) {
        return playerStats.get(playerUuid);
    }
    
    /**
     * Check if player has statistics tracked.
     */
    public boolean hasStats(UUID playerUuid) {
        return playerStats.containsKey(playerUuid);
    }
    
    /**
     * Reset statistics for a specific player.
     */
    public void resetStats(UUID playerUuid) {
        MiningStats stats = playerStats.get(playerUuid);
        if (stats != null) {
            stats.reset();
        }
    }
    
    /**
     * Remove player from tracking entirely.
     */
    public void removePlayer(UUID playerUuid) {
        playerStats.remove(playerUuid);
    }
    
    /**
     * Get total number of players being tracked.
     */
    public int getTrackedPlayerCount() {
        return playerStats.size();
    }
    
    /**
     * Clear all player data (useful for testing or resets).
     */
    public void clearAll() {
        playerStats.clear();
    }
}

