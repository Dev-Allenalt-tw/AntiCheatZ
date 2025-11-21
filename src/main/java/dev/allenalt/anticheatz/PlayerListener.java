package dev.allenalt.anticheatz;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerListener implements Listener {
    
    private final AntiCheatZ plugin;
    
    public PlayerListener(AntiCheatZ plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getBanManager().isBanned(player.getUniqueId())) {
            long timeLeft = plugin.getBanManager().getTimeLeft(player.getUniqueId());
            String reason = plugin.getBanManager().getReason(player.getUniqueId());
            String formattedTime = plugin.getBanManager().formatTimeLeft(timeLeft);
            
            String banMessage = plugin.getMessageManager().getMessageWithoutPrefix("ban-message")
                    .replace("%player%", player.getName())
                    .replace("%reason%", reason)
                    .replace("%time%", formattedTime);
            
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, banMessage);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
    }
    
    private boolean shouldBanPlayer(Player player) {
        
        return false;
    }
}
