package me.sgray.plugin.pumpkincarving;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class PumpkinCarving extends JavaPlugin implements Listener {
    ArrayList<String> pLore = new ArrayList<String>();
    List<Location> placed = new LinkedList<Location>();
    WEUtil weUtil;

    public void onDisable() {
        pLore.clear();
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        pLore.add("Uncarved");
        if (getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            weUtil = new WEUtil(this);
        }
    }

    @EventHandler
    public void onPumpkinGrowth(BlockGrowEvent event) {
        if (!event.getNewState().getType().equals(Material.PUMPKIN)) {
            return;
        }
        BlockState bState = event.getNewState();
        bState.setType(Material.PUMPKIN);
        bState.setRawData((byte) 4);
        bState.update();
    }

    @EventHandler
    public void onPumpkinBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.PUMPKIN)) {
            return;
        }
        if (!event.isCancelled()) {
            if (event.getBlock().getData() == (byte) 4 && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                Location pLoc = event.getBlock().getLocation();
                event.setCancelled(true);

                ItemStack ucPumpkin = new ItemStack(Material.PUMPKIN, 1);
                ItemMeta meta = ucPumpkin.getItemMeta();
                meta.setLore(pLore);
                ucPumpkin.setItemMeta(meta);

                pLoc.getBlock().setType(Material.AIR);
                pLoc.getWorld().dropItem(pLoc, ucPumpkin);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPumpkinPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getType().equals(Material.PUMPKIN)) {
            return;
        }
        if (event.canBuild()) {
            if (event.getItemInHand().hasItemMeta() && event.getItemInHand().getItemMeta().getLore().equals(pLore)) {
                placed.add(event.getBlock().getLocation());
                defaceTimer();
            }
        }
    }

    @EventHandler
    public void onPumpkinInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR)) {
            return;
        }
        if (event.getClickedBlock().getType().equals(Material.PUMPKIN) && event.getClickedBlock().getData() == (byte) 4) {
            ItemStack inHand = event.getPlayer().getItemInHand();
            if (inHand.getType().equals(Material.WOOD_AXE) || inHand.getType().equals(Material.STONE_AXE)
                    || inHand.getType().equals(Material.IRON_AXE) || inHand.getType().equals(Material.GOLD_AXE)
                    || inHand.getType().equals(Material.DIAMOND_AXE)) {
                if (weUtil != null) {
                    if (weUtil.isWand(inHand) && weUtil.hasActiveSession(event.getPlayer())) {
                        return;
                    }
                }
                event.getClickedBlock().setData(getFaceByte(event.getBlockFace()));
                event.getPlayer().getItemInHand().setDurability((short) (event.getItem().getDurability() + 1));
            }
        }
    }

    private byte getFaceByte(BlockFace face) {
        switch (face) {
            case SOUTH:
                return (byte) 0;
            case WEST:
                return (byte) 1;
            case NORTH:
                return (byte) 2;
            case EAST:
                return (byte) 3;
            default:
                return (byte) 0;
        }
    }

    private void defaceTimer() {
        getServer().getScheduler().runTask(this, new Runnable() {
            public void run() {
                defacePumpkins();
            }
        }).getTaskId();
    }

    private void defacePumpkins() {
        for (Location loc : placed) {
            Block block = loc.getWorld().getBlockAt(loc);
            if (block.getType().equals(Material.PUMPKIN)) {
                block.setData((byte) 4);
            }
        }
    }
}
