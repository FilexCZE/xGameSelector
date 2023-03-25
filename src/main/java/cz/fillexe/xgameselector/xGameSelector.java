package cz.fillexe.xgameselector;

import cz.fillexe.xgameselector.function.Commands;
import cz.fillexe.xgameselector.function.Crafting;
import cz.fillexe.xgameselector.function.Selector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public final class xGameSelector extends JavaPlugin implements CommandExecutor {
    private static xGameSelector instance;
    private FileConfiguration config;
    private Selector selector;

    public void onEnable() {
        instance = this;
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
        // Zavolejte metodu init() místo loadSigns()
        selector.init();

        getCommand("xgameselector").setExecutor(new Commands());
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

