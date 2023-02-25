package snw.rfm.littletoys.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface DropCallback {

    void onClick(Player player, ItemStack stack);
}
