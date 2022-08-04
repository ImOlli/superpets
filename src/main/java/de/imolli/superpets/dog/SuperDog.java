package de.imolli.superpets.dog;

import de.imolli.superpets.SuperPets;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.util.List;

public class SuperDog {

    private static final int RANGE = 100;
    private static final int KILL_DELAY_IN_MS = 20;

    private final Wolf wolf;
    private final Player player;

    private volatile boolean currentlyAttacking;

    public SuperDog(Wolf wolf, Player player) {
        this.wolf = wolf;
        this.player = player;
    }

    public void attackAllNearbyMonsters() {
        if (currentlyAttacking) {
            return;
        }

        currentlyAttacking = true;

        Location lastLocation = wolf.getLocation();

        wolf.setSitting(false);
        player.sendMessage(Component.text("§7Entering §cKILL MODE"));
        final List<Entity> nearbyEntities = wolf.getNearbyEntities(RANGE, RANGE, RANGE);

        if (nearbyEntities.isEmpty()) {
            player.sendMessage(Component.text("§7There are no enemies near you."));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(SuperPets.getPlugin(SuperPets.class), () -> {
            int killedCount = 0;

            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity) {

                    if (((LivingEntity) entity).getHealth() == 0) {
                        System.out.println("Some entity is already death idk");
                        continue;
                    }

                    if (!isBadEntity(entity)) {
                        continue;
                    }

                    killedCount++;

                    Bukkit.getScheduler().runTask(SuperPets.getPlugin(SuperPets.class), () -> {
                        wolf.teleport(entity.getLocation());
                        Location location = wolf.getLocation();
                        World world = wolf.getLocation().getWorld();

                        world.spawnParticle(Particle.EXPLOSION_NORMAL, location, 100, 0.5, 0.5, 0.5, 0.15);
                        world.spawnParticle(Particle.FLAME, location, 50, 0.5, 0.5, 0.5, 0);
                        world.playSound(location, Sound.ENTITY_CHICKEN_EGG, 0.5F, 1F);

                        ((LivingEntity) entity).damage(((LivingEntity) entity).getHealth() + 2);
                    });

                    try {
                        Thread.sleep(KILL_DELAY_IN_MS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            final int count = killedCount;

            Bukkit.getScheduler().runTask(SuperPets.getPlugin(SuperPets.class), () -> {
                currentlyAttacking = false;
                wolf.setSitting(true);
                wolf.teleport(lastLocation);
                player.sendMessage(Component.text("§7Finished! I killed §e" + count + " §7enemies!"));
            });
        });

    }

    private static boolean isBadEntity(Entity entity) {
        switch (entity.getType()) {
            case WARDEN:
            case SKELETON:
            case SLIME:
            case CAVE_SPIDER:
            case EVOKER:
            case WITCH:
            case WITHER_SKELETON:
            case ZOGLIN:
            case ZOMBIFIED_PIGLIN:
            case PIGLIN:
            case SPIDER:
            case BLAZE:
            case STRAY:
            case STRIDER:
            case PILLAGER:
            case ZOMBIE:
            case ZOMBIE_HORSE:
            case ZOMBIE_VILLAGER:
            case WITHER:
            case ENDER_DRAGON:
                return true;
            default:
                return false;
        }
    }

    public static void playSpawnAnimation(Location loc, Runnable callback) {
        Location location = loc.add(0, 1, 0);

        World world = location.getWorld();
        world.spawnParticle(Particle.ENCHANTMENT_TABLE, location, 500, 0, 0, 0, 1);
        world.playSound(location, Sound.ENTITY_WITHER_SHOOT, 0.3F, -1F);

        Bukkit.getScheduler().runTaskLater(SuperPets.getPlugin(SuperPets.class), () -> {
            world.spawnParticle(Particle.EXPLOSION_NORMAL, location, 100, 0.5, 0.5, 0.5, 0.15);
            world.spawnParticle(Particle.FLAME, location, 20, 0.5, 0.5, 0.5, 0);
            world.playSound(location, Sound.ENTITY_WITHER_DEATH, 0.3F, 1F);
            world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.3F, 0.2F);
            callback.run();
        }, 30);

    }

    public Wolf getWolf() {
        return wolf;
    }

    public boolean isCurrentlyAttacking() {
        return currentlyAttacking;
    }
}
