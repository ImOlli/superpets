package de.imolli.superpets;

import de.imolli.superpets.dog.SuperDogHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class SuperPets extends JavaPlugin {

    @Override
    public void onEnable() {
        SuperPets.getPlugin(SuperPets.class).getLogger().log(Level.INFO, "Checking all wolfes...");

        SuperDogHandler.checkAllWolfes();
        SuperDogHandler.reregisterAllSuperDogs();

        registerEvents();
        registerCommands();
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(SuperDogHandler.getInstance(), this);
    }

    private void registerCommands() {
        PluginCommand superpet = Objects.requireNonNull(getCommand("superpet"));
        superpet.setExecutor(SuperPetCommand.getInstance());
        superpet.setTabCompleter(SuperPetCommand.getInstance());
    }

}
