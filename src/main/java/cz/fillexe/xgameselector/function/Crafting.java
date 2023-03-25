package cz.fillexe.xgameselector.function;

import cz.fillexe.xgameselector.xGameSelector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.NamespacedKey;

public class Crafting {
    private xGameSelector plugin;

    public Crafting(xGameSelector plugin) {
        this.plugin = plugin;
    }

    public void removeRecipe(Material material) {
        NamespacedKey key = new NamespacedKey(this.plugin, material.toString());
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(material));
        Bukkit.getServer().removeRecipe(recipe.getKey());
    }
}
