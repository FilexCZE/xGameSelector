package cz.fillexe.xgameselector.function;

import cz.fillexe.xgameselector.xGameSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        ConfigurationSection signs = xGameSelector.getInstance().getConfig().getConfigurationSection("Signs");
        for (String signName : signs.getKeys(false)) {
            ConfigurationSection sign = signs.getConfigurationSection(signName);
            int x = sign.getInt("position.x");
            int y = sign.getInt("position.y");
            int z = sign.getInt("position.z");
            String worldName = sign.getString("position.world");
            World world = Bukkit.getWorld(worldName);
            Location signLocation = new Location(world, x, y, z);

            if (signLocation.getBlock().getState() instanceof Sign) {
                Sign signBlock = (Sign) signLocation.getBlock().getState();

                ConfigurationSection additionalPositions = sign.getConfigurationSection("additional_position");
                boolean shouldShowAdditional = false;
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

                    double minX = Math.min(pos1.getX(), pos2.getX());
                    double minY = Math.min(pos1.getY(), pos2.getY());
                    double minZ = Math.min(pos1.getZ(), pos2.getZ());
                    double maxX = Math.max(pos1.getX(), pos2.getX());
                    double maxY = Math.max(pos1.getY(), pos2.getY());
                    double maxZ = Math.max(pos1.getZ(), pos2.getZ());

                    Location checkLocation = eyeLocation.clone();

                    for (int i = 0; i < 100; i++) {
                        checkLocation.add(direction);
                        double checkX = checkLocation.getX();
                        double checkY = checkLocation.getY();
                        double checkZ = checkLocation.getZ();

                        if (checkX >= minX && checkX <= maxX && checkY >= minY && checkY <= maxY && checkZ >= minZ && checkZ <= maxZ) {
                            shouldShowAdditional = true;
                            break;
                        }
                    }
                }

                String signText = "";
                if (shouldShowAdditional) {
                    ConfigurationSection additionalText = sign.getConfigurationSection("additional_text");
                    if (additionalText != null) {
                        signText += additionalText.getString("line1") + "\n";
                        signText += additionalText.getString("line2") + "\n";
                        signText += additionalText.getString("line3") + "\n";
                        signText += additionalText.getString("line4") + "\n";
                    }
                } else {
                    ConfigurationSection permanentText = sign.getConfigurationSection("permanent_text");
                    if (permanentText != null) {
                        signText += permanentText.getString("line1") + "\n";
                        signText += permanentText.getString("line2") + "\n";
                        signText += permanentText.getString("line3") + "\n";
                        signText += permanentText.getString("line4") + "\n";
                    }
                    // update sign text
                    signBlock.setLine(0, signText.split("\n").length > 0 && signText.split("\n")[0] != null ? signText.split("\n")[0] : "");
                    signBlock.setLine(1, signText.split("\n").length > 1 && signText.split("\n")[1] != null ? signText.split("\n")[1] : "");
                    signBlock.setLine(2, signText.split("\n").length > 2 && signText.split("\n")[2] != null ? signText.split("\n")[2] : "");
                    signBlock.setLine(3, signText.split("\n").length > 3 && signText.split("\n")[3] != null ? signText.split("\n")[3] : "");
                    signBlock.update();
                }
            }
        }
    }
}