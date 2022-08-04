package de.imolli.superpets.dog;

import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.persistence.PersistentDataType;

public final class SuperDogFactory {

    private SuperDogFactory() {
        // Factory-Class don't need a constructor
    }

    public static SuperDog createSuperDogFromEntity(Player player, Wolf wolf) {
        wolf.setAI(false);

        SuperDog superDog = new SuperDog(wolf, player);

        SuperDog.playSpawnAnimation(wolf.getLocation(), () -> {
            wolf.setCollarColor(DyeColor.BLUE);
            wolf.setGlowing(true);
            wolf.setSilent(true);
            wolf.setSitting(true);
            wolf.setTamed(true);
            wolf.setCanPickupItems(true);
            wolf.setCustomNameVisible(true);
            wolf.customName(Component.text("§5Super Dog"));
            wolf.getPersistentDataContainer().set(SuperDogHandler.getDogKey(), PersistentDataType.SHORT, (short) 1);

            player.sendMessage(Component.text("§7Your dog is now a §5SUPER DOG"));
            player.playSound(wolf.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.2F, 1F);
        });

        SuperDogHandler.getInstance().registerSuperDog(player, superDog);

        return superDog;
    }

}
