package dev.allenalt.anticheatz;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BanManager {
    
    private final AntiCheatZ plugin;
    private static final long BAN_DURATION = TimeUnit.HOURS.toMillis(24); // 24 hours
    
    public BanManager(AntiCheatZ plugin) {
        this.plugin = plugin;
    }
    
    public void banPlayer(Player player, String reason) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        long currentTime = System.currentTimeMillis();
        long banEndTime = currentTime + BAN_DURATION;
        
        // Save to data.yml
        FileConfiguration data = plugin.getDataConfig();
        String path = "bans." + uuid.toString();
        
        data.set(path + ".uuid", uuid.toString());
        data.set(path + ".name", name);
        data.set(path + ".reason", reason);
        data.set(path + ".ban-time", currentTime);
        data.set(path + ".ban-end-time", banEndTime);
        data.set(path + ".remaining-time", BAN_DURATION);
        
        plugin.saveBanData();
        
        // Apply Minecraft ban
        Date expiration = new Date(banEndTime);
        Bukkit.getBanList(BanList.Type.NAME).addBan(name, reason, expiration, "AntiCheatZ");
        
        // Kick player
        String kickMessage = plugin.getMessageManager().getMessage("kick-message")
                .replace("%player%", name)
                .replace("%reason%", reason)
                .replace("%time%", "24 hours");
        
        player.kickPlayer(kickMessage);
        
        // Broadcast
        String broadcast = plugin.getMessageManager().getMessage("broadcast-message")
                .replace("%player%", name)
                .replace("%reason%", reason);
        
        Bukkit.broadcastMessage(broadcast);
        
        // Console log
        String logMessage = plugin.getMessageManager().getMessage("detected-log-message")
                .replace("%player%", name)
                .replace("%reason%", reason);
        
        plugin.getLogger().info(logMessage);
    }
    
    public boolean isBanned(UUID uuid) {
        FileConfiguration data = plugin.getDataConfig();
        String path = "bans." + uuid.toString();
        
        if (!data.contains(path)) {
            return false;
        }
        
        long banEndTime = data.getLong(path + ".ban-end-time");
        long currentTime = System.currentTimeMillis();
        
        if (currentTime >= banEndTime) {
            // Ban expired, remove it
            data.set(path, null);
            plugin.saveBanData();
            return false;
        }
        
        return true;
    }
    
    public long getTimeLeft(UUID uuid) {
        FileConfiguration data = plugin.getDataConfig();
        String path = "bans." + uuid.toString();
        
        if (!data.contains(path)) {
            return 0;
        }
        
        long banEndTime = data.getLong(path + ".ban-end-time");
        long currentTime = System.currentTimeMillis();
        long timeLeft = banEndTime - currentTime;
        
        return Math.max(0, timeLeft);
    }
    
    public String getReason(UUID uuid) {
        FileConfiguration data = plugin.getDataConfig();
        String path = "bans." + uuid.toString();
        
        if (!data.contains(path)) {
            return "Unknown";
        }
        
        return data.getString(path + ".reason", "Unknown");
    }
    
    public String formatTimeLeft(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
    
    public void unbanPlayer(UUID uuid) {
        FileConfiguration data = plugin.getDataConfig();
        String path = "bans." + uuid.toString();
        
        if (data.contains(path)) {
            String name = data.getString(path + ".name");
            data.set(path, null);
            plugin.saveBanData();
            
            // Remove Minecraft ban
            if (name != null) {
                Bukkit.getBanList(BanList.Type.NAME).pardon(name);
            }
        }
    }
}
