package com.grinderwolf.smw.plugin.commands;

import com.grinderwolf.smw.plugin.commands.sub.GotoCmd;
import com.grinderwolf.smw.plugin.commands.sub.HelpCmd;
import com.grinderwolf.smw.plugin.commands.sub.LoadWorldCmd;
import com.grinderwolf.smw.plugin.commands.sub.MigrateWorldCmd;
import com.grinderwolf.smw.plugin.commands.sub.Subcommand;
import com.grinderwolf.smw.plugin.commands.sub.UnloadWorldCmd;
import com.grinderwolf.smw.plugin.commands.sub.UnlockWorldCmd;
import com.grinderwolf.smw.plugin.commands.sub.VersionCmd;
import com.grinderwolf.smw.plugin.commands.sub.WorldListCmd;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandManager implements CommandExecutor {

    public static final String PREFIX = ChatColor.BLUE + ChatColor.BOLD.toString() + "SMW " + ChatColor.GRAY + ">> ";

    @Getter
    private static CommandManager instance;
    private Map<String, Subcommand> commands = new HashMap<>();

    /* A list containing all the worlds that are being performed operations on, so two commands cannot be run at the same time */
    @Getter
    private final Set<String> worldsInUse = new HashSet<>();

    public CommandManager() {
        instance = this;

        commands.put("help", new HelpCmd());
        commands.put("version", new VersionCmd());
        commands.put("goto", new GotoCmd());
        commands.put("load", new LoadWorldCmd());
        commands.put("unload", new UnloadWorldCmd());
        commands.put("unlock", new UnlockWorldCmd());
        commands.put("list", new WorldListCmd());
        commands.put("migrate", new MigrateWorldCmd());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(PREFIX + ChatColor.AQUA + "Slime World Manager" + ChatColor.GRAY + " is a plugin that implements the Slime Region Format, " +
                    "designed by the Hypixel Dev Team to load and save worlds more efficiently. To check out the help page, type "
                    + ChatColor.YELLOW + "/smw help" + ChatColor.GRAY + ".");

            return true;
        }

        Subcommand command = commands.get(args[0]);

        if (command == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Unknown command. To check out the help page, type " + ChatColor.GRAY + "/smw help" + ChatColor.RED + ".");

            return true;
        }

        if (command.inGameOnly() && !(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "This command can only be run in-game.");

            return true;
        }

        if (!command.getPermission().equals("") && !sender.hasPermission(command.getPermission()) && !sender.hasPermission("smw.*")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "You do not have permission to perform this command.");

            return true;
        }

        String[] subCmdArgs = new String[args.length - 1];
        System.arraycopy(args,1, subCmdArgs, 0, subCmdArgs.length);

        if (!command.onCommand(sender, subCmdArgs)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Command usage: /smw " + ChatColor.GRAY + command.getUsage() + ChatColor.RED + ".");
        }

        return true;
    }

    public Collection<Subcommand> getCommands() {
        return commands.values();
    }
}
