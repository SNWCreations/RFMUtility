package snw.rfm.littletoys;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PlayerSlow extends BukkitRunnable implements Listener {
    private static final int FREEZE_TIME = 5; // in seconds
    private static BlockData ICE;

    private final Main main;
    private final Player player;
    private int ticks;

    public PlayerSlow(Main main, Player player) {
        this.main = main;
        this.player = player;
        this.ticks = FREEZE_TIME * 20;
        if (ICE == null) {
            ICE = player.getServer().createBlockData(Material.ICE);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer() == player) {
            e.setCancelled(true);
        }
    }

    @Override
    public void run() {
        if (ticks-- > 0) {
            Objects.requireNonNull(player.getLocation().getWorld())
                    .spawnParticle(Particle.BLOCK_DUST, player.getLocation(), 5, ICE);
        } else {
            cleanup();
        }
    }

    private void cleanup() {
        HandlerList.unregisterAll(this);
        cancel();
        this.main.serviceProvider.unfreeze(player);
    }

    public void start() {
        runTaskTimer(main, 0L, 1L);
        this.main.getServer().getPluginManager().registerEvents(this, main);
    }
}
