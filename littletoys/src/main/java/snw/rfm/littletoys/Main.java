package snw.rfm.littletoys;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import snw.rfm.littletoys.item.ItemBuilder;

public final class Main extends JavaPlugin implements Listener {
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 30, 2, false, false);
    private static final PotionEffect INVISIBLE = new PotionEffect(PotionEffectType.INVISIBILITY, 10, 1, false, false);
    private ItemStack snowball;
    private ItemStack speedBlock;
    private ItemStack invisibilityBlock;
    ServiceProvider serviceProvider;
    ItemDropDispatcher itemDropDispatcher;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // region init item
        snowball = new ItemBuilder(Material.SNOWBALL)
                .setDisplayName(ChatColor.RED + "冰冻球")
                .setLore(ChatColor.GREEN + "使被击中的猎人被冰冻 5 秒，在此期间猎人的抓捕行为无效。")
                .toItemStack();
        speedBlock = new ItemBuilder(Material.DIAMOND_BLOCK)
                .setDisplayName("速度方块")
                .setLore(
                        ChatColor.BLUE + "速度 III (0:05)",
                        "",
                        ChatColor.GREEN + "这是瞬间生效的药水！扔出 (一般为 Q 键) 即可！"
                )
                .toItemStack();
        invisibilityBlock = new ItemBuilder(Material.GRAY_CONCRETE)
                .setDisplayName("隐身方块")
                .setLore(
                        ChatColor.BLUE + "隐身 (0:10)",
                        "",
                        ChatColor.GREEN + "这是瞬间生效的药水！扔出 (一般为 Q 键) 即可！"
                )
                .toItemStack();
        // endregion

        itemDropDispatcher = new ItemDropDispatcher(this);
        init();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getScheduler().cancelTasks(this);
    }

    private void init() {
        itemDropDispatcher.register(speedBlock, (player, stack) -> player.addPotionEffect(SPEED));
        itemDropDispatcher.register(invisibilityBlock, (player, stack) -> player.addPotionEffect(INVISIBLE));

//        if (getServer().getPluginManager().getPlugin("RunForMoney2") != null) {
//            initRFMSystem();
//        } else {
//            initInternalSystem();
//        }
        initInternalSystem();
    }

    private void initInternalSystem() {
        // getLogger().warning("未检测到有效的 RunForMoney 插件 (要求版本 v2.x)，正在注册纯原版实现...");
        serviceProvider = new InternalServiceProvider();
    }

    private void initRFMSystem() {
        // TODO
    }

    private boolean isHunter(Player player) {
        return serviceProvider.isHunter(player);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e instanceof Snowball) {
            if (e.getHitEntity() instanceof Player) {
                Player hitPlayer = (Player) e.getHitEntity();
                ItemStack item = ((Snowball) e).getItem();
                if (snowball.isSimilar(item)) {
                    if (isHunter(hitPlayer)) {
                        new PlayerSlow(this, hitPlayer).start();
                        serviceProvider.freeze(hitPlayer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (isHunter((Player) e.getDamager())) {
                if (serviceProvider.isFreeze(((Player) e.getDamager()))) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
