package snw.rfm.littletoys;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ItemRegistry {
    public static final Map<String, ItemStack> MAP = new HashMap<String, ItemStack>() {
        @Override
        public ItemStack put(String key, ItemStack value) {
            return super.put(key, toRealKey(value));
        }

        @Override
        public ItemStack get(Object key) {
            return Optional.ofNullable(super.get(key)).map(ItemStack::clone).orElse(null);
        }
    };

    public static ItemStack toRealKey(ItemStack st) {
        ItemStack result = st.clone();
        result.setAmount(1);
        return result;
    }
}
