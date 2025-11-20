package dev.allenalt.anticheatz;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class AntiCheatZ extends JavaPlugin {
    
    private static AntiCheatZ instance;
    private FileConfiguration messagesConfig;
    private FileConfiguration dataConfig;
    private File messagesFile;
    private File dataFile;
    private BanManager banManager;
    private MessageManager messageManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Create plugin folder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Load configurations
        loadConfigs();
        
        // Initialize managers
        this.messageManager = new MessageManager(this);
        this.banManager = new BanManager(this);
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Register commands
        getCommand("anticheatz").setExecutor(new AntiCheatZCommand(this));
        
        // Register PlaceholderAPI if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AntiCheatZPlaceholders(this).register();
            getLogger().info("PlaceholderAPI hooked successfully!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }
        
        getLogger().info("AntiCheatZ v1.0.0 has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save data before shutdown
        saveBanData();
        getLogger().info("AntiCheatZ has been disabled!");
    }
    
    private void loadConfigs() {
        // Create messages.yml
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Create data.yml
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public void reloadConfigs() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        messageManager.reload();
    }
    
    public void saveBanData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("Could not save data.yml!");
            e.printStackTrace();
        }
    }
    
    public static AntiCheatZ getInstance() {
        return instance;
    }
    
    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }
    
    public FileConfiguration getDataConfig() {
        return dataConfig;
    }
    
    public BanManager getBanManager() {
        return banManager;
    }
    
    public MessageManager getMessageManager() {
        return messageManager;
    }
}
