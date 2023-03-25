package cz.fillexe.xgameselector.function;

import cz.fillexe.xgameselector.xGameSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Material;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;

public class Selector implements Listener {

    private final Map<String, String> signTexts = new HashMap<>();

    public void loadSigns() {
        ConfigurationSection signs = xGameSelector.getInstance().getConfig().getConfigurationSection("Signs");
        for (String signName : signs.getKeys(false)) {
            ConfigurationSection sign = signs.getConfigurationSection(signName);
            signTexts.put(signName, sign.getString("permanent_text"));
        }
    }

    public Selector() {
        FileConfiguration config = xGameSelector.getInstance().getConfig();
        if (!config.contains("BlockedRecipes")) {
            List<String> defaultBlockedRecipes = Arrays.asList("CRIMSON_SIGN");
            config.set("BlockedRecipes", defaultBlockedRecipes);
        }
        if (!config.contains("Signs")) {
            config.createSection("Signs");
        }
        ConfigurationSection signs = config.getConfigurationSection("Signs");
        for (String signName : signs.getKeys(false)) {
            ConfigurationSection sign = signs.getConfigurationSection(signName);
            if (!sign.contains("additional_text")) {
                sign.set("additional_text", "");
            }
            if (!sign.contains("permanent_text")) {
                sign.set("permanent_text", "");
            }
            if (!sign.contains("additional_position")) {
                ConfigurationSection position = sign.createSection("additional_position");
                for (int i = 1; i <= 2; i++) {
                    ConfigurationSection p = position.createSection(String.valueOf(i));
                    p.set("x", 0);
                    p.set("y", 0);
                    p.set("z", 0);
                    p.set("world", "world");
                }
            }
            if (!sign.contains("sign.position")) {
                ConfigurationSection position = sign.createSection("sign.position");
                position.set("x", 0);
                position.set("y", 0);
                position.set("z", 0);
                position.set("world", "world");
            }
        }
        xGameSelector.getInstance().saveConfig();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location eyeLocation = player.getEyeLocation();
        Block block = eyeLocation.getBlock().getRelative(0, -1, 0);
        if (block.getType() == Material.CRIMSON_SIGN || block.getType() == Material.CRIMSON_WALL_HANGING_SIGN || block.getType() == Material.CRIMSON_WALL_SIGN) {
            Sign signBlock = (Sign) block.getState();
            ConfigurationSection signs = xGameSelector.getInstance().getConfig().getConfigurationSection("Signs");
            for (String signName : signs.getKeys(false)) {
                ConfigurationSection sign = signs.getConfigurationSection(signName);
                int x = sign.getInt("position.x");
                int y = sign.getInt("position.y");
                int z = sign.getInt("position.z");
                String worldName = sign.getString("position.world");
                World world = Bukkit.getWorld(worldName);
                Location signLocation = new Location(world, x, y, z);
                if (signLocation.equals(block.getLocation())) {
                    ConfigurationSection additionalPositions = sign.getConfigurationSection("additional_position");
                    if (additionalPositions != null) {
                        boolean shouldShowAdditional = false;
                        for (String posName : additionalPositions.getKeys(false)) {
                            ConfigurationSection additionalPosition = additionalPositions.getConfigurationSection(posName);
                            int ax = additionalPosition.getInt("x");
                            int ay = additionalPosition.getInt("y");
                            int az = additionalPosition.getInt("z");
                            String aWorldName = additionalPosition.getString("world");
                            World aWorld = Bukkit.getWorld(aWorldName);
                            Location additionalLocation = new Location(aWorld, ax, ay, az);
                            if (eyeLocation.getX() >= additionalLocation.getX() && eyeLocation.getX() <= additionalLocation.getX() + 1
                                    && eyeLocation.getY() >= additionalLocation.getY() && eyeLocation.getY() <= additionalLocation.getY() + 1
                                    && eyeLocation.getZ() >= additionalLocation.getZ() && eyeLocation.getZ() <= additionalLocation.getZ() + 1) {
                                shouldShowAdditional = true;
                                break;
                            }
                        }
                        if (shouldShowAdditional) {
                            signTexts.put(signName, sign.getString("additional_text"));
                        } else {
                            signTexts.put(signName, sign.getString("permanent_text"));
                        }
                    } else {
                        signTexts.put(signName, sign.getString("permanent_text"));
                    }
                    signBlock.setLine(0, signTexts.get(signName));
                    signBlock.update();
                }
            }
        }
    }
}