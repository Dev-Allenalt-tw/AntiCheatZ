package dev.allenalt.anticheatz;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AntiCheatZPlaceholders extends PlaceholderExpansion {
    
    private final AntiCheatZ plugin;
    
    public AntiCheatZPlaceholders(AntiCheatZ plugin) {
        this.plugin = plugin;
    }
    
    @Override
    @NotNull
    public String getIdentifier() {
        return "anticheatz";
    }
    
    @Override
    @NotNull
    public String getAuthor() {
        return "Dev_Allenalt_tw";
    }
    
    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        
        if (player == null) {
            return "";
        }
        
        // %anticheatz_banned%
        if (identifier.equals("banned")) {
            return plugin.getBanManager().isBanned(player.getUniqueId()) ? "Yes" : "No";
        }
        
        // %anticheatz_reason%
        if (identifier.equals("reason")) {
            if (plugin.getBanManager().isBanned(player.getUniqueId())) {
                return plugin.getBanManager().getReason(player.getUniqueId());
            }
            return "Not banned";
        }
        
        // %anticheatz_timeleft%
        if (identifier.equals("timeleft")) {
            if (plugin.getBanManager().isBanned(player.getUniqueId())) {
                long timeLeft = plugin.getBanManager().getTimeLeft(player.getUniqueId());
                return plugin.getBanManager().formatTimeLeft(timeLeft);
            }
            return "0s";
        }
        
        return null;
    }
}
