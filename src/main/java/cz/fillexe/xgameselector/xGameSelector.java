package cz.fillexe.xgameselector;

import org.bukkit.Bukkit;
import cz.fillexe.xgameselector.function.Crafting;
import cz.fillexe.xgameselector.function.Selector;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

public final class xGameSelector extends JavaPlugin {
    private FileConfiguration config;
    private Selector selector;
    private static xGameSelector instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        List<String> blockedRecipes = config.getStringList("BlockedRecipes");
        Crafting crafting = new Crafting(this);
        for (String blockedRecipe : blockedRecipes) {
            Material material = Material.getMaterial(blockedRecipe);
            crafting.removeRecipe(material);
        }

        selector = new Selector();
        Bukkit.getServer().getPluginManager().registerEvents(selector, this);
        selector.loadSigns();
        instance = this;

        Bukkit.getServer().getLogger().info("Plugin byl zapnut");
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getLogger().info("Plugin byl vypnut");
    }

    public static xGameSelector getInstance() {
        return instance;
    }
}

