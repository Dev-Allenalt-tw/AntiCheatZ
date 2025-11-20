package dev.allenalt.anticheatz;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class AntiCheatZCommand implements CommandExecutor {
    
    private final AntiCheatZ plugin;
    
    public AntiCheatZCommand(AntiCheatZ plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 0) {
            sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &fv1.0.0 by Dev_Allenalt_tw"));
            sender.sendMessage(plugin.getMessageManager().translateColors("&7/anticheatz reload &f- Reload configurations"));
            sender.sendMessage(plugin.getMessageManager().translateColors("&7/anticheatz unban <player> &f- Unban a player"));
            sender.sendMessage(plugin.getMessageManager().translateColors("&7/anticheatz check <player> &f- Check ban status"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("anticheatz.reload")) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cNo permission!"));
                return true;
            }
            
            plugin.reloadConfigs();
            sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &aConfigurations reloaded!"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("unban")) {
            if (!sender.hasPermission("anticheatz.unban")) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cNo permission!"));
                return true;
            }
            
            if (args.length < 2) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cUsage: /anticheatz unban <player>"));
                return true;
            }
            
            String playerName = args[1];
            Player target = Bukkit.getPlayer(playerName);
            UUID uuid = target != null ? target.getUniqueId() : getUUIDFromDataFile(playerName);
            
            if (uuid == null) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cPlayer not found!"));
                return true;
            }
            
            if (!plugin.getBanManager().isBanned(uuid)) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cPlayer is not banned!"));
                return true;
            }
            
            plugin.getBanManager().unbanPlayer(uuid);
            sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &aPlayer unbanned successfully!"));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("check")) {
            if (!sender.hasPermission("anticheatz.check")) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cNo permission!"));
                return true;
            }
            
            if (args.length < 2) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cUsage: /anticheatz check <player>"));
                return true;
            }
            
            String playerName = args[1];
            Player target = Bukkit.getPlayer(playerName);
            UUID uuid = target != null ? target.getUniqueId() : getUUIDFromDataFile(playerName);
            
            if (uuid == null) {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cPlayer not found!"));
                return true;
            }
            
            if (plugin.getBanManager().isBanned(uuid)) {
                long timeLeft = plugin.getBanManager().getTimeLeft(uuid);
                String reason = plugin.getBanManager().getReason(uuid);
                String formattedTime = plugin.getBanManager().formatTimeLeft(timeLeft);
                
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cPlayer is banned!"));
                sender.sendMessage(plugin.getMessageManager().translateColors("&7Reason: &f" + reason));
                sender.sendMessage(plugin.getMessageManager().translateColors("&7Time left: &f" + formattedTime));
            } else {
                sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &aPlayer is not banned!"));
            }
            return true;
        }
        
        sender.sendMessage(plugin.getMessageManager().translateColors("&8[&aAntiCheatZ&8] &7» &cUnknown subcommand!"));
        return true;
    }
    
    private UUID getUUIDFromDataFile(String playerName) {
        for (String key : plugin.getDataConfig().getConfigurationSection("bans").getKeys(false)) {
            String name = plugin.getDataConfig().getString("bans." + key + ".name");
            if (name != null && name.equalsIgnoreCase(playerName)) {
                return UUID.fromString(key);
            }
        }
        return null;
    }
}
