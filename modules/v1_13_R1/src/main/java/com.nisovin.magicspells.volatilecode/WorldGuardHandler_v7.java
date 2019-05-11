package com.nisovin.magicspells.volatilecode;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.compat.CompatBasics;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

// Put v7 handler in 1.13 module as 1.13 is the earliest version it supports
public class WorldGuardHandler_v7 implements IWorldGuardHandler {

    @Override
    public boolean inZone(Location location, String worldName, String regionName) {
        // Check world
        if (!worldName.equals(location.getWorld().getName())) return false;

        ProtectedRegion region = null;
        if (region == null) {
            WorldGuard worldGuard = WorldGuard.getInstance();
            WorldGuardPlugin wgPlugin = null;
            if (CompatBasics.pluginEnabled("WorldGuard")) wgPlugin = (WorldGuardPlugin)CompatBasics.getPlugin("WorldGuard");
            if (wgPlugin != null) {
                World w = Bukkit.getServer().getWorld(worldName);
                if (w != null) {
                    RegionManager rm = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w));
                    if (rm != null)
                        region = rm.getRegion(regionName);
                }
            }
        }

        // Check if contains
        if (region != null) {
            BlockVector3 v = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            return region.contains(v);
        }
        MagicSpells.error("Failed to access WorldGuard region '" + regionName + '\'');
        return false;
    }

    @Override
    public ApplicableRegionSet getRegion(Location loc) {
        return getRegionManager(loc.getWorld()).getApplicableRegions(BukkitAdapter.asBlockVector(loc));
    }

    @Override
    public RegionManager getRegionManager(World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    }

    @Override
    public ProtectedRegion getTopPriorityRegion(Location loc) {
        ApplicableRegionSet regions = getRegion(loc);
        ProtectedRegion topRegion = null;
        int topPriority = Integer.MIN_VALUE;
        for (ProtectedRegion region: regions) {
            if (region.getPriority() > topPriority) {
                topRegion = region;
                topPriority = region.getPriority();
            }
        }
        return topRegion;
    }

    @Override
    public Flag<?>[] getFlags() {
        List<Flag<?>> flags = WorldGuard.getInstance().getFlagRegistry().getAll();
        return flags.toArray(new Flag[flags.size()]);
    }
}
