package com.nisovin.magicspells.castmodifiers.conditions;

import com.nisovin.magicspells.MagicSpells;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.nisovin.magicspells.castmodifiers.Condition;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public abstract class AbstractWorldGuardCondition extends Condition {

	protected WorldGuardPlugin worldGuard;
	
	protected boolean worldGuardEnabled() {
		worldGuard = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
		return !(worldGuard == null || !worldGuard.isEnabled());
	}
	
	protected RegionManager getRegionManager(World world) {
		return MagicSpells.getVolatileCodeHandler().getWorldGuardHandler().getRegionManager(world);
	}
	
	protected ApplicableRegionSet getRegion(Location loc) {
		return MagicSpells.getVolatileCodeHandler().getWorldGuardHandler().getRegion(loc);
	}
	
	protected ProtectedRegion getTopPriorityRegion(Location loc) {
		return MagicSpells.getVolatileCodeHandler().getWorldGuardHandler().getTopPriorityRegion(loc);
	}
	
}
