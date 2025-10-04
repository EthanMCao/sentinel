package dev.ethancao.antixray;

import dev.ethancao.antixray.logging.JsonLogger;
import dev.ethancao.antixray.logging.XrayEvent;
import dev.ethancao.antixray.tracking.MiningStats;
import dev.ethancao.antixray.tracking.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class Sentinel extends JavaPlugin implements Listener {
    
    private PlayerDataManager dataManager;
    private JsonLogger jsonLogger;

    @Override
    public void onEnable() {
        getLogger().info("Sentinel Anti-Xray plugin enabled!");
        
        // Initialize data manager
        this.dataManager = new PlayerDataManager();
        
        // Initialize JSON logger
        this.jsonLogger = new JsonLogger(getDataFolder(), getLogger());
        
        // Register event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        
        getLogger().info("Mining tracker and JSON logging initialized!");
        getLogger().info("Logs will be written to: " + jsonLogger.getLogFilePath());
    }

    @Override
    public void onDisable() {
        getLogger().info("Sentinel Anti-Xray plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sentinel")) {
            // Permission check
            if (!sender.hasPermission("sentinel.admin")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            
            // No arguments - show usage
            if (args.length == 0) {
                sendUsageMessage(sender);
                return true;
            }
            
            // Handle subcommands
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "stats":
                    return handleStatsCommand(sender, args);
                case "reset":
                    return handleResetCommand(sender, args);
                case "flag":
                    return handleFlagCommand(sender, args);
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + args[0]);
                    sendUsageMessage(sender);
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Display command usage information.
     */
    private void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Sentinel Anti-Xray ===");
        sender.sendMessage(ChatColor.YELLOW + "/sentinel stats <player>" + ChatColor.GRAY + " - View mining statistics");
        sender.sendMessage(ChatColor.YELLOW + "/sentinel reset <player>" + ChatColor.GRAY + " - Reset player statistics");
        sender.sendMessage(ChatColor.YELLOW + "/sentinel flag <player>" + ChatColor.GRAY + " - Manually flag player");
    }
    
    /**
     * Handle /sentinel stats <player>
     */
    private boolean handleStatsCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /sentinel stats <player>");
            return true;
        }
        
        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);
        
        // Try to find player by name (online or offline)
        UUID targetUuid = null;
        String displayName = targetName;
        
        if (target != null) {
            // Player is online
            targetUuid = target.getUniqueId();
            displayName = target.getName();
        } else {
            // Try to find by offline player
            @SuppressWarnings("deprecation")
            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
            if (offlinePlayer.hasPlayedBefore()) {
                targetUuid = offlinePlayer.getUniqueId();
                displayName = offlinePlayer.getName();
            }
        }
        
        if (targetUuid == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return true;
        }
        
        // Get stats
        MiningStats stats = dataManager.getStats(targetUuid);
        if (stats == null || stats.getTotalStoneBlocks() == 0 && stats.getTotalOresMined() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "No mining data for " + displayName);
            return true;
        }
        
        // Display stats
        sender.sendMessage(ChatColor.GOLD + "=== Mining Stats: " + displayName + " ===");
        sender.sendMessage(ChatColor.AQUA + "Ores Mined:");
        sender.sendMessage(ChatColor.WHITE + "  Diamonds: " + ChatColor.AQUA + stats.getDiamondsMined());
        sender.sendMessage(ChatColor.WHITE + "  Emeralds: " + ChatColor.GREEN + stats.getEmeraldsMined());
        sender.sendMessage(ChatColor.WHITE + "  Iron: " + ChatColor.GRAY + stats.getIronMined());
        sender.sendMessage(ChatColor.WHITE + "  Gold: " + ChatColor.GOLD + stats.getGoldMined());
        sender.sendMessage(ChatColor.WHITE + "  Coal: " + ChatColor.DARK_GRAY + stats.getCoalMined());
        sender.sendMessage(ChatColor.WHITE + "  Lapis: " + ChatColor.BLUE + stats.getLapisMined());
        sender.sendMessage(ChatColor.WHITE + "  Redstone: " + ChatColor.RED + stats.getRedstoneMined());
        
        sender.sendMessage(ChatColor.AQUA + "Stone Blocks: " + ChatColor.WHITE + stats.getTotalStoneBlocks());
        sender.sendMessage(ChatColor.AQUA + "Total Ores: " + ChatColor.WHITE + stats.getTotalOresMined());
        
        // Ratios
        double oreRatio = stats.getOreToStoneRatio();
        double diamondRatio = stats.getDiamondToStoneRatio();
        sender.sendMessage(ChatColor.AQUA + "Ore/Stone Ratio: " + ChatColor.WHITE + String.format("%.4f", oreRatio));
        sender.sendMessage(ChatColor.AQUA + "Diamond/Stone Ratio: " + ChatColor.WHITE + String.format("%.4f", diamondRatio));
        
        // Suspicion
        int suspicionScore = stats.getSuspicionScore();
        ChatColor scoreColor = suspicionScore >= 20 ? ChatColor.RED : 
                               suspicionScore >= 10 ? ChatColor.YELLOW : 
                               ChatColor.GREEN;
        sender.sendMessage(ChatColor.AQUA + "Suspicion Score: " + scoreColor + suspicionScore);
        
        if (stats.getManualFlags() > 0) {
            sender.sendMessage(ChatColor.RED + "Manual Flags: " + stats.getManualFlags());
        }
        
        return true;
    }
    
    /**
     * Handle /sentinel reset <player>
     */
    private boolean handleResetCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /sentinel reset <player>");
            return true;
        }
        
        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);
        
        UUID targetUuid = null;
        String displayName = targetName;
        
        if (target != null) {
            targetUuid = target.getUniqueId();
            displayName = target.getName();
        } else {
            @SuppressWarnings("deprecation")
            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
            if (offlinePlayer.hasPlayedBefore()) {
                targetUuid = offlinePlayer.getUniqueId();
                displayName = offlinePlayer.getName();
            }
        }
        
        if (targetUuid == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return true;
        }
        
        if (!dataManager.hasStats(targetUuid)) {
            sender.sendMessage(ChatColor.YELLOW + "No data to reset for " + displayName);
            return true;
        }
        
        dataManager.resetStats(targetUuid);
        sender.sendMessage(ChatColor.GREEN + "Reset mining statistics for " + displayName);
        getLogger().info(sender.getName() + " reset stats for " + displayName);
        
        return true;
    }
    
    /**
     * Handle /sentinel flag <player>
     */
    private boolean handleFlagCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /sentinel flag <player>");
            return true;
        }
        
        String targetName = args[1];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player must be online: " + targetName);
            return true;
        }
        
        MiningStats stats = dataManager.getOrCreateStats(target.getUniqueId(), target.getName());
        stats.addManualFlag();
        stats.addSuspicionPoints(15); // Add suspicion points for manual flag
        
        sender.sendMessage(ChatColor.GREEN + "Manually flagged " + target.getName() + " (Total flags: " + stats.getManualFlags() + ")");
        getLogger().warning(sender.getName() + " manually flagged " + target.getName() + " for suspicious activity");
        
        return true;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        
        // Get or create stats for this player
        MiningStats stats = dataManager.getOrCreateStats(player.getUniqueId(), player.getName());
        
        // Track ore mining
        switch (blockType) {
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                stats.incrementDiamonds();
                checkSuspiciousActivity(player, stats, "DIAMOND");
                break;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                stats.incrementIron();
                break;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
                stats.incrementGold();
                break;
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                stats.incrementEmeralds();
                checkSuspiciousActivity(player, stats, "EMERALD");
                break;
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                stats.incrementLapis();
                break;
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                stats.incrementRedstone();
                break;
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                stats.incrementCoal();
                break;
                
            // Track stone/filler blocks
            case STONE:
            case COBBLESTONE:
            case ANDESITE:
            case DIORITE:
            case GRANITE:
            case TUFF:
                stats.incrementStone();
                break;
            case DEEPSLATE:
            case COBBLED_DEEPSLATE:
                stats.incrementDeepslate();
                break;
            default:
                // Ignore other blocks (dirt, gravel, etc.)
                break;
        }
    }
    
    /**
     * Check if mining behavior is suspicious and log if needed.
     */
    private void checkSuspiciousActivity(Player player, MiningStats stats, String oreType) {
        int totalStone = stats.getTotalStoneBlocks();
        int valuableOres = stats.getDiamondsMined() + stats.getEmeraldsMined() + stats.getGoldMined();
        double diamondRatio = stats.getDiamondToStoneRatio();
        
        boolean suspicious = false;
        String reason = "";
        int suspicionPoints = 0;
        
        // DETECTION 1: Pure ore mining (catches xray users mining without stone)
        // This detects players who go straight to ores without breaking through rock
        if (valuableOres >= 5 && totalStone < 20) {
            suspicious = true;
            reason = String.format("Pure ore mining detected: %d valuable ores with only %d stone (likely xray)", 
                valuableOres, totalStone);
            suspicionPoints = 20; // High suspicion - this is very unnatural
        }
        // DETECTION 2: Extremely suspicious - many ores with very little stone (but some mining)
        else if (valuableOres >= 8 && totalStone < 50) {
            suspicious = true;
            reason = String.format("Very low stone mining: %d valuable ores with only %d stone", 
                valuableOres, totalStone);
            suspicionPoints = 15;
        }
        // DETECTION 3: High ratio detection (original - requires 50+ stone baseline)
        else if (totalStone >= 50) {
            if (diamondRatio > 0.05) { // More than 1 diamond per 20 stone
                suspicious = true;
                reason = String.format("Very high diamond ratio: %.3f (diamonds: %d, stone: %d)", 
                    diamondRatio, stats.getDiamondsMined(), totalStone);
                suspicionPoints = 10;
            } else if (diamondRatio > 0.02) { // More than 1 diamond per 50 stone
                suspicious = true;
                reason = String.format("High diamond ratio: %.3f (diamonds: %d, stone: %d)", 
                    diamondRatio, stats.getDiamondsMined(), totalStone);
                suspicionPoints = 5;
            }
        }
        
        // Log and add suspicion points if flagged
        if (suspicious) {
            stats.addSuspicionPoints(suspicionPoints);
            
            // Console log
            getLogger().warning(String.format("[SUSPICIOUS] %s mined %s - %s (Score: %d, +%d points)",
                player.getName(), oreType, reason, stats.getSuspicionScore(), suspicionPoints));
            
            // JSON log
            String detectionType = suspicionPoints == 20 ? "pure_ore_mining" :
                                   suspicionPoints == 15 ? "low_stone_mining" :
                                   "high_ratio";
            
            XrayEvent event = new XrayEvent(
                jsonLogger.getCurrentTimestamp(),
                player.getName(),
                player.getUniqueId().toString(),
                oreType.toLowerCase() + "_mine",
                "UNKNOWN", // We don't track exact ore type in this context
                player.getLocation(),
                stats.getTotalStoneBlocks(),
                stats.getDiamondsMined(),
                stats.getTotalOresMined(),
                stats.getDiamondToStoneRatio(),
                stats.getSuspicionScore(),
                detectionType,
                reason
            );
            
            jsonLogger.logEvent(event);
        }
    }
}

