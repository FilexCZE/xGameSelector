package cz.fillexe.xgameselector.function;

import com.google.common.collect.Sets;
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
import org.bukkit.util.Vector;
import org.checkerframework.framework.qual.TargetLocations;

import java.util.HashMap;
import java.util.Map;

public class Selector implements Listener {

    private final Map<String, String> signTexts = new HashMap<>();

    public void init() {
        loadSigns();
    }

    public void loadSigns() {
        ConfigurationSection signs = xGameSelector.getInstance().getConfig().getConfigurationSection("Signs");
        for (String signName : signs.getKeys(false)) {
            ConfigurationSection sign = signs.getConfigurationSection(signName);
            signTexts.put(signName, sign.getString("permanent_text"));
        }
    }

    private String getSignText(ConfigurationSection sign, String textType) {
        ConfigurationSection textSection = sign.getConfigurationSection(textType);
        StringBuilder signText = new StringBuilder();

        if (textSection != null) {
            signText.append(textSection.getString("line1", "")).append("\n");
            signText.append(textSection.getString("line2", "")).append("\n");
            signText.append(textSection.getString("line3", "")).append("\n");
            signText.append(textSection.getString("line4", "")).append("\n");
        }

        return signText.toString();
    }

    private boolean isPlayerLookingAt(Player player, ConfigurationSection sign) {

        ConfigurationSection additionalPositions = sign.getConfigurationSection("additional_position");

        if (additionalPositions != null && !additionalPositions.getKeys(false).isEmpty()) {
            Location pos1 = new Location(
                    Bukkit.getWorld(additionalPositions.getString("pos1.world")),
                    additionalPositions.getInt("pos1.x"),
                    additionalPositions.getInt("pos1.y"),
                    additionalPositions.getInt("pos1.z")
            );
            Location pos2 = new Location(
                    Bukkit.getWorld(additionalPositions.getString("pos2.world")),
                    additionalPositions.getInt("pos2.x"),
                    additionalPositions.getInt("pos2.y"),
                    additionalPositions.getInt("pos2.z")
            );

            Block targetBlock = player.getTargetBlock(Sets.newHashSet(Material.AIR), 15);
            Location targetLocation = targetBlock.getLocation();
            targetLocation = targetLocation.add(0.5, 0.5, 0.5);
            return IsInCuboin3D(targetLocation,pos1,pos2);
        }

        return false;
    }

    private void updateSignText(Sign signBlock, String signText) {
        signBlock.setLine(0, signText.split("\n").length > 0 && signText.split("\n")[0] != null ? signText.split("\n")[0] : "");
        signBlock.setLine(1, signText.split("\n").length > 1 && signText.split("\n")[1] != null ? signText.split("\n")[1] : "");
        signBlock.setLine(2, signText.split("\n").length > 2 && signText.split("\n")[2] != null ? signText.split("\n")[2] : "");
        signBlock.setLine(3, signText.split("\n").length > 3 && signText.split("\n")[3] != null ? signText.split("\n")[3] : "");
        signBlock.update();
    }

    public void updateSignTextForPlayer(Player player) {
        ConfigurationSection signs = xGameSelector.getInstance().getConfig().getConfigurationSection("Signs");
        for (String signName : signs.getKeys(false)) {
            ConfigurationSection sign = signs.getConfigurationSection(signName);
            int x = sign.getInt("position.x");
            int y = sign.getInt("position.y");
            int z = sign.getInt("position.z");
            String worldName = sign.getString("position.world");
            World world = Bukkit.getWorld(worldName);
            Location signLocation = new Location(world, x, y, z);

            Block block = signLocation.getBlock();
            if (block.getState() instanceof Sign) {
                Sign signBlock = (Sign) block.getState();

                // Get the permanent text
                String signText = getSignText(sign, "permanent_text");

                // Check if the player is looking at the specified coordinates
                if (isPlayerLookingAt(player, sign)) {
                    String additionalText = getSignText(sign, "additional_text");
                    if (!additionalText.isEmpty()) {
                        signText = additionalText;
                    }
                }

                // Update the sign text
                updateSignText(signBlock, signText);
            }
        }
    }

    private static boolean IsInCuboin3D(Location loca, Location pos1, Location pos2) {
        double x = loca.getX();
        double y = loca.getY();
        double z = loca.getZ();

        double xMin = Math.min(pos1.getX(), pos2.getX());
        double xMax = Math.max(pos1.getX(), pos2.getX()) + 0.99;
        double yMin = Math.min(pos1.getY(), pos2.getY());
        double yMax = Math.max(pos1.getY(), pos2.getY()) + 0.99;
        double zMin = Math.min(pos1.getZ(), pos2.getZ());
        double zMax = Math.max(pos1.getZ(), pos2.getZ()) + 0.99;

        return x>=xMin && x<=xMax && y>=yMin && y<=yMax && z>=zMin && z<=zMax;
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        updateSignTextForPlayer(player);
    }
}