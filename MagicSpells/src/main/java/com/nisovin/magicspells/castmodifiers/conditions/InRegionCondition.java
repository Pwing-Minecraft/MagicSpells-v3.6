package com.nisovin.magicspells.castmodifiers.conditions;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.util.compat.CompatBasics;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nisovin.magicspells.castmodifiers.Condition;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class InRegionCondition extends Condition {

	WorldGuardPlugin worldGuard;
	String worldName;
	String regionName;
	
	@Override
	public boolean setVar(String var) {
		if (var == null) return false;
		
		worldGuard = (WorldGuardPlugin)CompatBasics.getPlugin("WorldGuard");
		if (worldGuard == null || !worldGuard.isEnabled()) return false;
		
		String[] split = var.split(":");
		if (split.length == 2) {
			worldName = split[0];
			regionName = split[1];
			return true;
		}
		return false;
	}

	@Override
	public boolean check(Player player) {
		return check(player, player.getLocation());
	}

	@Override
	public boolean check(Player player, LivingEntity target) {
		return check(player, target.getLocation());
	}

	@Override
	public boolean check(Player player, Location location) {
		return MagicSpells.getVolatileCodeHandler().getWorldGuardHandler().inZone(location, worldName, regionName);
	}
}
