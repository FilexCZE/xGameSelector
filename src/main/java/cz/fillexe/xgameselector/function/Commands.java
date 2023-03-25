package cz.fillexe.xgameselector.function;

import cz.fillexe.xgameselector.xGameSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private final xGameSelector plugin = xGameSelector.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
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

            Location location = player.getLocation();
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
            player.sendMessage("Sign " + signName + " was created successfully!");
            return true;
        } else {
            player.sendMessage("Error: Subcommand '" + args[0] + "' does not exist");
            return false;
        }
    }
}