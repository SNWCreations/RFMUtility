package snw.rfm.littletoys.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.function.Consumer;

public class ItemBuilder {
    private final ItemStack stack;

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack.clone();
    }

    public ItemBuilder setDisplayName(String displayName) {
        modifyMeta(i -> i.setDisplayName(ChatColor.RESET + displayName));
        return this;
    }

    public ItemBuilder setLore(String... lores) {
        modifyMeta(i -> i.setLore(Arrays.asList(lores)));
        return this;
    }

    private void modifyMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = this.stack.getItemMeta();
        consumer.accept(meta);
        this.stack.setItemMeta(meta);
    }

    public ItemStack toItemStack() {
        return stack.clone();
    }
}
