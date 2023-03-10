package snw.rfm.littletoys;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import snw.rfm.littletoys.item.ItemBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Main extends JavaPlugin implements Listener {
    private NamespacedKey SNOWBALL_KEY = new NamespacedKey(this, "snowball");
    private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2, false, false);
    private static final PotionEffect INVISIBLE = new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 1, false, false);
    private ItemStack snowball;
    ServiceProvider serviceProvider;
    ItemDropDispatcher itemDropDispatcher;

    @Override
    public void onEnable() {
        // Plugin startup logic
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
        // region init item
        snowball = new ItemBuilder(Material.SNOWBALL)
                .setDisplayName(ChatColor.BOLD + "" + ChatColor.RED + "冰冻球")
                .setLore(ChatColor.GREEN + "使被击中的猎人被冰冻 5 秒，在此期间猎人的抓捕行为无效。")
                .toItemStack();
        ItemMeta meta = snowball.getItemMeta();
        meta.getPersistentDataContainer().set(SNOWBALL_KEY, PersistentDataType.INTEGER, 1);
        snowball.setItemMeta(meta);

        ItemStack speedBlock = new ItemBuilder(Material.DIAMOND_BLOCK)
                .setDisplayName(ChatColor.BOLD + "" + ChatColor.BLUE + "速度方块")
                .setLore(
                        ChatColor.BLUE + "速度 III (0:05)",
                        "",
                        ChatColor.GREEN + "这是瞬间生效的药水！扔出 (一般为 Q 键) 即可！"
                )
                .toItemStack();
        ItemStack invisibilityBlock = new ItemBuilder(Material.GRAY_CONCRETE)
                .setDisplayName(ChatColor.BOLD + "隐身方块")
                .setLore(
                        ChatColor.BLUE + "隐身 (0:10)",
                        "",
                        ChatColor.GREEN + "这是瞬间生效的药水！扔出 (一般为 Q 键) 即可！"
                )
                .toItemStack();
        // endregion

        ItemRegistry.MAP.put("speed_block", speedBlock);
        ItemRegistry.MAP.put("invisibility_block", invisibilityBlock);
        ItemRegistry.MAP.put("snowball", snowball);

//        if (getServer().getPluginManager().getPlugin("RunForMoney2") != null) {
//            initRFMService();
//        } else {
//            initInternalService();
//        }
        initInternalService();

        itemDropDispatcher.register(speedBlock, (player, stack) -> player.addPotionEffect(SPEED));
        itemDropDispatcher.register(invisibilityBlock, (player, stack) -> player.addPotionEffect(INVISIBLE));
        getServer().getPluginManager().registerEvents(itemDropDispatcher, this);
        getCommand("littletoy").setExecutor(this);
    }

    private void initInternalService() {
        // getLogger().warning("未检测到有效的 RunForMoney 插件 (要求版本 v2.x)，正在注册纯原版实现...");
        serviceProvider = new InternalServiceProvider();
    }

    private void initRFMService() {
        // TODO
    }

    private boolean isHunter(Player player) {
        return serviceProvider.isHunter(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "需要一个参数。");
                return false;
            }
            if (ItemRegistry.MAP.containsKey(args[0])) {
                ((Player) sender).getInventory().addItem(ItemRegistry.MAP.get(args[0]));
            } else {
                sender.sendMessage(ChatColor.RED + "找不到物品。");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "不支持的执行者类型。要求是玩家。");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0 || args.length == 1) {
                return new ArrayList<>(ItemRegistry.MAP.keySet());
            }
        }
        return Collections.emptyList();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.GREEN + "此服务器正在运行 LittleToys 道具插件，版本 " + getDescription().getVersion());
        event.getPlayer().sendMessage(ChatColor.GREEN + "作者: ZX夏夜之风 (B站 UID: 57486712)");
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball) {
            if (e.getHitEntity() instanceof Player) {
                Player hitPlayer = (Player) e.getHitEntity();
                ItemStack item = ((Snowball) e.getEntity()).getItem();
                if (item.getItemMeta().getPersistentDataContainer().has(SNOWBALL_KEY, PersistentDataType.INTEGER)) {
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
