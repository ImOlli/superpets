package de.imolli.superpets.dog;

import de.imolli.superpets.SuperPets;
import de.imolli.superpets.item.SuperItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SuperDogHandler implements Listener {

    private final Map<Player, List<SuperDog>> superDogs = new HashMap<>();

    private static SuperDogHandler instance;

    @EventHandler
    public void onTameAnimal(EntityTameEvent event) {
        if (event.getEntity().getType().equals(EntityType.WOLF)) {
            event.getEntity().setCanPickupItems(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getType().equals(EntityType.WOLF)) {
            if (findSuperDogByEntity(event.getEntity()).map(SuperDog::isCurrentlyAttacking).orElse(false)) {
                event.setCancelled(true);
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // SuperDogs will be registered on interact again.
        superDogs.remove(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType().equals(EntityType.WOLF)) {
            Wolf wolf = (Wolf) event.getRightClicked();

            if (isSuperDog(wolf) && event.getPlayer().isSneaking()) {
                SuperDog dog = findSuperDogByEntity(wolf).orElseGet(() -> registerWolfAsSuperDog(event.getPlayer(), wolf));

                dog.attackAllNearbyMonsters();
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!event.getEntity().getType().equals(EntityType.WOLF)) {
            return;
        }

        Wolf entity = (Wolf) event.getEntity();

        if (!isSuperDog(entity) && SuperItemHandler.isSuperItem(event.getItem())) {
            // Remove the dropped item
            event.getItem().remove();
            final UUID thrower = event.getItem().getThrower();

            Player player = Optional.ofNullable(thrower)
                    .map(Bukkit::getPlayer)
                    .filter(OfflinePlayer::isOnline)
                    .orElse(null);

            if (player == null) {
                throw new IllegalStateException("Could not find player of dog");
            }

            SuperDogFactory.createSuperDogFromEntity(player, entity);
        } else {
            event.setCancelled(true);
        }
    }

    public void registerSuperDog(Player player, SuperDog superDog) {
        superDogs.computeIfAbsent(player, p -> new ArrayList<>())
                .add(superDog);
    }

    public Optional<SuperDog> findSuperDogByEntity(Entity entity) {
        return superDogs.values().stream()
                .map(list -> list.stream()
                        .filter(dog -> dog.getWolf().equals(entity))
                        .findAny()
                        .orElse(null)
                )
                .filter(Objects::nonNull)
                .findAny();
    }

    public static void checkAllWolfes() {
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream()
                    .filter(type -> type.getType().equals(EntityType.WOLF))
                    .map(Wolf.class::cast)
                    .filter(Tameable::isTamed)
                    .forEach(wolf -> wolf.setCanPickupItems(true));
        }
    }

    public static void reregisterAllSuperDogs() {
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream()
                    .filter(type -> type.getType().equals(EntityType.WOLF))
                    .map(Wolf.class::cast)
                    .filter(SuperDogHandler::isSuperDog)
                    .forEach(dog -> SuperDogHandler.getInstance().registerSuperDog(((Player) dog.getOwner()), new SuperDog(dog, ((Player) dog.getOwner()))));
        }
    }

    public static boolean isSuperDog(Entity entity) {
        return Optional.ofNullable(entity.getPersistentDataContainer().get(getDogKey(), PersistentDataType.SHORT))
                .map(data -> data == (short) 1)
                .orElse(false);
    }

    @NotNull
    public static NamespacedKey getDogKey() {
        return new NamespacedKey(SuperPets.getPlugin(SuperPets.class), "superdog");
    }

    public static SuperDogHandler getInstance() {
        if (instance == null) {
            instance = new SuperDogHandler();
        }

        return instance;
    }

    private SuperDog registerWolfAsSuperDog(Player player, Wolf wolf) {
        final SuperDog superDog = new SuperDog(wolf, player);
        registerSuperDog(player, superDog);

        return superDog;
    }

}
