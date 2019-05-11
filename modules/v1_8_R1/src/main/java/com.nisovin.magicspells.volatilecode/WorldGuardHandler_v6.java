package com.nisovin.magicspells.volatilecode;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.compat.CompatBasics;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

// Put v6 handler in 1.8 module as 1.8 is the earliest version it supports
public class WorldGuardHandler_v6 implements IWorldGuardHandler {

    @Override
    public boolean inZone(Location location, String worldName, String regionName) {
        // Check world
        if (!worldName.equals(location.getWorld().getName())) return false;

        ProtectedRegion region = null;
        WorldGuardPlugin worldGuard = null;
        if (CompatBasics.pluginEnabled("WorldGuard")) worldGuard = (WorldGuardPlugin)CompatBasics.getPlugin("WorldGuard");
        if (worldGuard != null) {
            World w = Bukkit.getServer().getWorld(worldName);
            if (w != null) {
                RegionManager rm = getRegionManager(w);
                if (rm != null)
                    region = rm.getRegion(regionName);

            }
        }

        // Check if contains
        if (region != null) {
            com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ());
            return region.contains(v);
        }
        MagicSpells.error("Failed to access WorldGuard region '" + regionName + '\'');
        return false;
    }

    @Override
    public ApplicableRegionSet getRegion(Location loc) {
        return getRegionManager(loc.getWorld()).getApplicableRegions(new Vector(loc.getX(), loc.getY(), loc.getZ()));
    }

    @Override
    public RegionManager getRegionManager(World world) {
        return WGBukkit.getRegionManager(world);
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
        return DefaultFlag.getFlags();
    }
}
