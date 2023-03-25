package cz.fillexe.xgameselector.function;

import cz.fillexe.xgameselector.xGameSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.Arrays;

public class Commands implements CommandExecutor {
    private final xGameSelector plugin = xGameSelector.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("xgameselector.use")) {
            player.sendMessage("Nemáte oprávnění k použití tohoto příkazu.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage("Ahoj");
            return true;
        } else if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /xgameselector create <sign_name>");
                return false;
            }

            String signName = args[1];
            if (plugin.getConfig().contains("Signs." + signName)) {
                player.sendMessage("Error: Sign with name '" + signName + "' already exists");
                return false;
            }

            Block targetBlock = player.getTargetBlockExact(10);
            if (targetBlock == null) {
                player.sendMessage("Chyba: Nejste zaměřeni na žádný blok.");
                return false;
            }
            if (targetBlock.getType() == Material.CRIMSON_SIGN || targetBlock.getType() == Material.CRIMSON_WALL_HANGING_SIGN || targetBlock.getType() == Material.CRIMSON_WALL_SIGN) {
                Location location = targetBlock.getLocation();
                World world = location.getWorld();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                plugin.getConfig().set("Signs." + signName + ".position.x", x);
                plugin.getConfig().set("Signs." + signName + ".position.y", y);
                plugin.getConfig().set("Signs." + signName + ".position.z", z);
                plugin.getConfig().set("Signs." + signName + ".position.world", world.getName());
                plugin.getConfig().set("Signs." + signName + ".permanent_text", "");
                plugin.getConfig().set("Signs." + signName + ".additional_text", "");
                plugin.saveConfig();
                plugin.reloadConfig();
                player.sendMessage("Sign " + signName + " was created successfully!");
                return true;
            } else {
                player.sendMessage("Chyba: Nejste zaměřeni na ceduli.");
                return false;
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /xgameselector remove <sign_name>");
                return false;
            }

            String signName = args[1];
            if (!plugin.getConfig().contains("Signs." + signName)) {
                player.sendMessage("Error: Sign with name '" + signName + "' does not exist");
                return false;
            }
            plugin.getConfig().getConfigurationSection("Signs").set(signName, null);
            plugin.saveConfig();
            plugin.reloadConfig();
            player.sendMessage("Sign " + signName + " was removed successfully!");
            return true;
        } else if (args[0].equalsIgnoreCase("edit")) {
            if (args.length < 3) {
                player.sendMessage("Usage: /xgameselector edit <sign_name> <type> <options>");
                return false;
            }

            String signName = args[1];
            if (!plugin.getConfig().contains("Signs." + signName)) {
                player.sendMessage("Error: Sign with name '" + signName + "' does not exist");
                return false;
            }

            String editType = args[2];
            if (editType.equalsIgnoreCase("additional_text") || editType.equalsIgnoreCase("permanent_text")) {
                if (args.length < 5) {
                    player.sendMessage("Usage: /xgameselector edit <sign_name> " + editType + " [1,2,3,4] <text>");
                    return false;
                }

                int lineNumber = 0;
                try {
                    lineNumber = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Error: Invalid line number. Must be between 1 and 4.");
                    return false;
                }

                if (lineNumber < 1 || lineNumber > 4) {
                    player.sendMessage("Error: Line number must be between 1 and 4.");
                    return false;
                }

                String text = String.join(" ", Arrays.copyOfRange(args, 4, args.length));
                plugin.getConfig().set("Signs." + signName + "." + editType + ".line" + lineNumber, text);
                plugin.saveConfig();
                plugin.reloadConfig();
                player.sendMessage(editType + " for sign " + signName + " has been set to: " + text);

            } else if (editType.equalsIgnoreCase("position")) {
                if (args.length < 4) {
                    player.sendMessage("Usage: /xgameselector edit <sign_name> position pos1/pos2");
                    return false;
                }

                String positionType = args[3];
                if (!positionType.equalsIgnoreCase("pos1") && !positionType.equalsIgnoreCase("pos2")) {
                    player.sendMessage("Error: Invalid position type. Must be 'pos1' or 'pos2'.");
                    return false;
                }

                Location location = player.getLocation();
                World world = location.getWorld();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();

                plugin.getConfig().set("Signs." + signName + ".additional_position." + positionType + ".x", x);
                plugin.getConfig().set("Signs." + signName + ".additional_position." + positionType + ".y", y);
                plugin.getConfig().set("Signs." + signName + ".additional_position." + positionType + ".z", z);
                plugin.getConfig().set("Signs." + signName + ".additional_position." + positionType + ".world", world.getName());
                plugin.saveConfig();
                plugin.reloadConfig();

                player.sendMessage("Position " + positionType + " for sign " + signName + " has been set to: X=" + x + " Y=" + y + " Z=" + z + " World=" + world.getName());

            } else {
                player.sendMessage("Error: Invalid edit type '" + editType + "'. Must be 'additional_text', 'permanent_text' or 'position'.");
                return false;
            }
            return true;
        } else {
            player.sendMessage("Error: Subcommand '" + args[0] + "' does not exist");
            return false;
        }
    }
}