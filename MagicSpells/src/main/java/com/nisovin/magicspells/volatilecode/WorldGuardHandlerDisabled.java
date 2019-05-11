package com.nisovin.magicspells.volatilecode;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;

public class WorldGuardHandlerDisabled implements IWorldGuardHandler {

    @Override
    public boolean inZone(Location loc, String worldName, String regionName) {
        return false;
    }

    @Override
    public ApplicableRegionSet getRegion(Location loc) {
        return null;
    }

    @Override
    public RegionManager getRegionManager(World world) {
        return null;
    }

    @Override
    public ProtectedRegion getTopPriorityRegion(Location loc) {
        return null;
    }

    @Override
    public Flag<?>[] getFlags() {
        return new Flag[0];
    }
}
