package io.github.reconsolidated.itemprovider;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemProviderCommand implements CommandExecutor {
    private final ItemProvider itemProvider;

    public ItemProviderCommand(ItemProvider itemProvider) {
        this.itemProvider = itemProvider;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            onHelp(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Poprawne użycie: ");
                sender.sendMessage(ChatColor.AQUA + "/itemprovider add <name> <category> - dodaje item z ręki");
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.getInventory().getItemInMainHand() == null
                            || player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                        sender.sendMessage(ChatColor.RED + "Musisz trzymać item w ręce!");
                    } else {
                        String name = args[1];
                        String category = args[2];
                        itemProvider.addItem(player.getInventory().getItemInMainHand(), name, category);
                    }
                }
            }
        }
        return true;
    }

    private void onHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "ItemProvider help: ");
        sender.sendMessage(ChatColor.AQUA + "/itemprovider add <name> <category> - dodaje item z ręki");
    }
}
