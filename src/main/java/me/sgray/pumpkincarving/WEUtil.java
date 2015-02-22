package me.sgray.pumpkincarving;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class WEUtil {
    PumpkinCarving plugin;
    WorldEditPlugin wepl;

    protected WEUtil(PumpkinCarving plugin) {
        this.plugin = plugin;
        wepl = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    @SuppressWarnings("deprecation")
    protected boolean isWand(ItemStack item) {
        int wandItem = wepl.getLocalConfiguration().wandItem;
        return (item.getTypeId() == wandItem) ? true : false;
    }

    protected boolean hasActiveSession(Player player) {
        LocalSession session = wepl.getSession(player);
        return session.isToolControlEnabled();
    }
}
