package com.nisovin.magicspells.volatilecode;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;

public interface IWorldGuardHandler {

    boolean inZone(Location loc, String worldName, String regionName);

    ApplicableRegionSet getRegion(Location loc);
    RegionManager getRegionManager(World world);
    ProtectedRegion getTopPriorityRegion(Location loc);

    Flag<?>[] getFlags();
}
