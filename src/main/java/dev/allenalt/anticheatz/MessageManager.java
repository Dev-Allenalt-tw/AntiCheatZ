package dev.allenalt.anticheatz;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
    
    private final AntiCheatZ plugin;
    private final String prefix;
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
    
    public MessageManager(AntiCheatZ plugin) {
        this.plugin = plugin;
        this.prefix = translateColors("&8[&aAntiCheatZ&8] &7» ");
    }
    
    public String getMessage(String key) {
        FileConfiguration messages = plugin.getMessagesConfig();
        String message = messages.getString(key, "Message not found: " + key);
        return translateColors(prefix + message);
    }
    
    public String getMessageWithoutPrefix(String key) {
        FileConfiguration messages = plugin.getMessagesConfig();
        String message = messages.getString(key, "Message not found: " + key);
        return translateColors(message);
    }
    
    public String translateColors(String message) {
        if (message == null) {
            return "";
        }
        
        // First handle hex colors (format: &#FFFFFF or &FFFFFF)
        message = translateHexColors(message);
        
        // Then handle standard color codes
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    private String translateHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            StringBuilder magic = new StringBuilder("§x");
            
            for (char c : hexColor.substring(1).toCharArray()) {
                magic.append('§').append(c);
            }
            
            matcher.appendReplacement(buffer, magic.toString());
        }
        
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    public void reload() {
        plugin.reloadConfigs();
    }
}
