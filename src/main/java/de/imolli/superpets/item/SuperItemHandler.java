package de.imolli.superpets.item;

import de.imolli.superpets.SuperPets;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;

public class SuperItemHandler {
    public static boolean isSuperItem(Item item) {
        return item.getItemStack().getType().equals(Material.ENCHANTED_GOLDEN_APPLE);
    }

    public static NamespacedKey getKey() {
        return new NamespacedKey(SuperPets.getPlugin(SuperPets.class), "superitem");
    }

}