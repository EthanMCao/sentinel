package dev.ethancao.antixray.tracking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks mining statistics for a single player.
 * Used to detect xray-like behavior based on ore/stone ratios.
 */
public class MiningStats {
    private final UUID playerUuid;
    private final String playerName;
    
    // Track specific ore types
    private int diamondsMined = 0;
    private int ironMined = 0;
    private int goldMined = 0;
    private int emeraldsMined = 0;
    private int lapisMined = 0;
    private int redstoneMineds = 0;
    private int coalMined = 0;
    
    // Track stone/filler blocks
    private int stoneMined = 0;
    private int deepslateStoned = 0;
    
    // Timestamps
    private long firstMineTime;
    private long lastMineTime;
    
    // Suspicion tracking
    private int suspicionScore = 0;
    private int manualFlags = 0;
    
    public MiningStats(UUID playerUuid, String playerName) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.firstMineTime = System.currentTimeMillis();
        this.lastMineTime = System.currentTimeMillis();
    }
    
    // Increment methods for each ore type
    public void incrementDiamonds() { diamondsMined++; updateLastMineTime(); }
    public void incrementIron() { ironMined++; updateLastMineTime(); }
    public void incrementGold() { goldMined++; updateLastMineTime(); }
    public void incrementEmeralds() { emeraldsMined++; updateLastMineTime(); }
    public void incrementLapis() { lapisMined++; updateLastMineTime(); }
    public void incrementRedstone() { redstoneMineds++; updateLastMineTime(); }
    public void incrementCoal() { coalMined++; updateLastMineTime(); }
    
    public void incrementStone() { stoneMined++; updateLastMineTime(); }
    public void incrementDeepslate() { deepslateStoned++; updateLastMineTime(); }
    
    private void updateLastMineTime() {
        this.lastMineTime = System.currentTimeMillis();
    }
    
    // Getters
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public int getDiamondsMined() { return diamondsMined; }
    public int getIronMined() { return ironMined; }
    public int getGoldMined() { return goldMined; }
    public int getEmeraldsMined() { return emeraldsMined; }
    public int getLapisMined() { return lapisMined; }
    public int getRedstoneMined() { return redstoneMineds; }
    public int getCoalMined() { return coalMined; }
    public int getStoneMined() { return stoneMined; }
    public int getDeepslateMined() { return deepslateStoned; }
    
    public int getTotalOresMined() {
        return diamondsMined + ironMined + goldMined + emeraldsMined + 
               lapisMined + redstoneMineds + coalMined;
    }
    
    public int getTotalStoneBlocks() {
        return stoneMined + deepslateStoned;
    }
    
    public int getSuspicionScore() { return suspicionScore; }
    public void setSuspicionScore(int score) { this.suspicionScore = score; }
    public void addSuspicionPoints(int points) { this.suspicionScore += points; }
    
    public int getManualFlags() { return manualFlags; }
    public void addManualFlag() { this.manualFlags++; }
    
    public long getFirstMineTime() { return firstMineTime; }
    public long getLastMineTime() { return lastMineTime; }
    
    /**
     * Calculate the ratio of valuable ores to stone mined.
     * Higher ratio = more suspicious.
     */
    public double getOreToStoneRatio() {
        int totalStone = getTotalStoneBlocks();
        if (totalStone == 0) return 0.0;
        return (double) getTotalOresMined() / totalStone;
    }
    
    /**
     * Calculate diamond-specific ratio (most suspicious ore).
     */
    public double getDiamondToStoneRatio() {
        int totalStone = getTotalStoneBlocks();
        if (totalStone == 0) return 0.0;
        return (double) diamondsMined / totalStone;
    }
    
    /**
     * Reset all statistics (used by /sentinel reset command).
     */
    public void reset() {
        diamondsMined = 0;
        ironMined = 0;
        goldMined = 0;
        emeraldsMined = 0;
        lapisMined = 0;
        redstoneMineds = 0;
        coalMined = 0;
        stoneMined = 0;
        deepslateStoned = 0;
        suspicionScore = 0;
        manualFlags = 0;
        firstMineTime = System.currentTimeMillis();
        lastMineTime = System.currentTimeMillis();
    }
}

