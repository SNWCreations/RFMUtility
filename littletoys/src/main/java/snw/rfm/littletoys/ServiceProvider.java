package snw.rfm.littletoys;

import org.bukkit.entity.Player;

public interface ServiceProvider {
    void freeze(Player player);
    void unfreeze(Player player);
    boolean isFreeze(Player player);
    boolean isHunter(Player player);
    boolean isRunner(Player player);
}
