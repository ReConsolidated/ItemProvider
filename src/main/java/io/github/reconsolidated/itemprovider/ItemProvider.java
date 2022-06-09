package io.github.reconsolidated.itemprovider;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Name;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ItemProvider extends JavaPlugin implements Listener {
    private File dataFolder;
    private NamespacedKey nameKey;
    private Map<String, Map<String, ItemStack>> categories = new HashMap<>();

    public ItemStack getItem(String category, String name, int amount) {
        if (category.equalsIgnoreCase("money")) {
            return getMoneyItem(name, amount);
        }

        if (categories != null) {
            Map<String, ItemStack> cat = categories.get(category);
            if (cat == null) {
                return getNotFoundItem();
            }

            if (cat.containsKey(name)) {
                return cat.get(name).clone();
            }
        }
        return getNotFoundItem();
    }

    public void update() {
        categories = new HashMap<>();

        File[] files = dataFolder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) {
                String category = file.getName().split("\\.")[0];
                loadCategory(category);
            }
        }

    }

    private void loadCategory(String category) {
        YamlConfiguration config = CustomConfig.loadCustomConfig(category, dataFolder, true);

        if (config == null) return;

        Map<String, ItemStack> cat = new HashMap<>();
        for (String key : config.getKeys(false)) {
            ItemStack item = config.getItemStack(key);
            cat.put(key, item);
        }

        categories.put(category, cat);
    }

    private ItemStack getNotFoundItem(){
        return nameItem(new ItemStack(Material.DIRT, 1), Component.text("Item not found"));
    }

    private ItemStack getMoneyItem(String name, int amount) {
        ItemStack item = new ItemStack(Material.PAPER);
        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(this, "money_item"), PersistentDataType.INTEGER, amount);
        item.setItemMeta(meta);
        return nameItem(named(item, name), Component.text(name).color(TextColor.color(252,186,3)));
    }

    public List<ItemStack> getAllFromCategory(String category) {
        Map<String, ItemStack> cat = categories.get(category);
        if (cat == null) return new ArrayList<>();
        return new ArrayList<>(cat.values());
    }

    private ItemStack named(ItemStack item, String name) {
        item.getItemMeta().getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, name);
        return item;
    }

    public ItemStack getItem(String category, String name) {
        return getItem(category, name, 1);
    }

    public String getItemName(ItemStack item) {
        if (item.getItemMeta() == null) {
            return item.getType().name();
        }
        String name = item.getItemMeta().getPersistentDataContainer().get(nameKey, PersistentDataType.STRING);
        if (name == null) {
            return item.getType().name();
        }
        return name;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        nameKey = new NamespacedKey(this, "item_name");
        dataFolder = getDataFolder();
        update();
        getServer().getServicesManager().register(ItemProvider.class, this, this, ServicePriority.Normal);
        ExampleItems.init(this, this.getDataFolder());

        getCommand("itemprovider").setExecutor(new ItemProviderCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean addItem(ItemStack item, String name, String category) {
        YamlConfiguration config = CustomConfig.loadCustomConfig(category, dataFolder, true);
        if (config == null) return false;
        config.set(name, item);
        CustomConfig.saveCustomConfig(category, dataFolder, config);
        if (!categories.containsKey(category)) {
            categories.put(category, new HashMap<>());
        }
        categories.get(category).put(name, item);
        return true;
    }

    private static ItemStack nameItem(ItemStack item, Component displayName) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayName.style(Style.style().decoration(TextDecoration.ITALIC, false)));
        item.setItemMeta(meta);
        return item;
    }


    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().isRightClick() && event.getPlayer().getInventory().getItemInMainHand().equals(event.getItem())) {
            ItemMeta meta = event.getItem().getItemMeta();
            if (meta == null) return;
            Integer amount = meta.getPersistentDataContainer().get(new NamespacedKey(this, "money_item"), PersistentDataType.INTEGER);
            if (amount == null) return;
            getServer().dispatchCommand(getServer().getConsoleSender(), "eco give " + event.getPlayer().getName() + " " + amount);
            ItemStack item = event.getItem().clone();
            item.setAmount(1);
            event.getPlayer().getInventory().removeItem(item);

        }
    }
}
