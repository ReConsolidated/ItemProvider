package io.github.reconsolidated.itemprovider;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class ExampleItems {
    public static void init(Plugin plugin, File dataFolder) {
        YamlConfiguration config = CustomConfig.loadCustomConfig("example", dataFolder, true);
        assert config != null : "Couldn't load config: example";
        ItemStack plainPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        plainPickaxe.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        config.set("diamond_pickaxe_prot_1", plainPickaxe);

        ItemStack chestPlateWithKey = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta meta = chestPlateWithKey.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "test"),
                PersistentDataType.STRING, "example persistent data");
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "test_2"),
                PersistentDataType.INTEGER, 15);
        meta.setUnbreakable(true);
        meta.lore(List.of(Component.text("Line 1").color(TextColor.color(150, 12, 143)),
                Component.text("Line 2")));
        chestPlateWithKey.setItemMeta(meta);

        chestPlateWithKey.setAmount(5);
        chestPlateWithKey.addItemFlags(ItemFlag.HIDE_DESTROYS);
        config.set("chestplate_buffed", chestPlateWithKey);

        CustomConfig.saveCustomConfig("example", dataFolder, config);
    }
}
