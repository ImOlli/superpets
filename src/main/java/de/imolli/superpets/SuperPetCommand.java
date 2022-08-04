package de.imolli.superpets;

import de.imolli.superpets.dog.SuperDog;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SuperPetCommand implements CommandExecutor, TabCompleter {

    private static SuperPetCommand instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (args.length != 2) {
            return false;

        }

        if (args[0].equalsIgnoreCase("debug")) {
            if (args[1].equalsIgnoreCase("dog")) {
                SuperDog.playSpawnAnimation(((Player) sender).getLocation(), () -> {
                });

                return true;
            } else if (args[1].equalsIgnoreCase("mobs")) {
                for (int i = 0; i < 250; i++) {
                    ((Player) sender).getLocation().getWorld().spawnEntity(((Player) sender).getLocation(), EntityType.ZOMBIE);
                }

                return true;
            } else if (args[1].equalsIgnoreCase("warden")) {
                final Location location = ((Player) sender).getLocation().add(0, 10, 0);

                for (int i = 0; i < 100; i++) {
                    ((Player) sender).getLocation().getWorld().spawnEntity(location, EntityType.WARDEN);
                }

                return true;
            }
        }

        return false;
    }

    public static SuperPetCommand getInstance() {
        if (instance == null) {
            instance = new SuperPetCommand();
        }

        return instance;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return List.of("create", "debug");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("debug")) {
                return List.of("dog", "mobs", "warden");
            }
        }

        return null;
    }
}
