package snw.rfm.littletoys;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

class InternalServiceProvider implements ServiceProvider {
    private static final PotionEffect STRENGTH =
            new PotionEffect(
                    PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false, false
            );
    private final Set<String> freeze = new HashSet<>();

    @Override
    public void freeze(Player player) {
        freeze.add(player.getUniqueId().toString());
    }

    @Override
    public void unfreeze(Player player) {
        freeze.remove(player.getUniqueId().toString());
    }

    @Override
    public boolean isFreeze(Player player) {
        return freeze.contains(player.getUniqueId().toString());
    }

    @Override
    public boolean isHunter(Player player) {
        return player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE);
    }

    @Override
    public boolean isRunner(Player player) {
        return !isHunter(player);
    }
}
