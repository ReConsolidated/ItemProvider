package io.github.reconsolidated.itemprovider;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomConfig {
    public static YamlConfiguration loadCustomConfig(String name, File dataFolder, boolean createIfNotExists){
        YamlConfiguration customConfig = new YamlConfiguration();
        File customConfigFile = new File(dataFolder, name + ".yml");
        if (!customConfigFile.exists()) {
            if (!createIfNotExists) return null;
            try{
                customConfigFile.createNewFile();
            }
            catch (IOException e){
                Bukkit.getLogger().warning("Couldn't load config file: " + name);
            }
        }
        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return customConfig;
    }

    public static void saveCustomConfig(String name, File dataFolder, YamlConfiguration config){
        File customConfigFile = new File(dataFolder, name + ".yml");
        try {
            config.save(customConfigFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
