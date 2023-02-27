package snw.rfm.littletoys;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import snw.rfm.littletoys.item.DropCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ItemDropDispatcher implements Listener {
    private final Main main;
    private final Map<ItemStack, DropCallback> callbackMap;

    public ItemDropDispatcher(Main main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(this, this.main);
        this.callbackMap = new HashMap<>(); // only used in main thread, so we don't use ConcurrentHashMap
    }

    public void register(ItemStack stack, DropCallback callback) {
        this.callbackMap.put(toRealKey(stack), callback);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (main.serviceProvider.isRunner(e.getPlayer())) {
            ItemStack itemStack = e.getItemDrop().getItemStack();
            Optional.ofNullable(callbackMap.get(toRealKey(itemStack)))
                    .ifPresent(i -> {
                        i.onClick(e.getPlayer(), itemStack);
                        main.getServer().getScheduler().runTaskLater(main, () -> e.getItemDrop().remove(), 1L);
                    });
        }
    }

    private static ItemStack toRealKey(ItemStack st) {
        ItemStack result = st.clone();
        result.setAmount(1);
        return result;
    }
}
