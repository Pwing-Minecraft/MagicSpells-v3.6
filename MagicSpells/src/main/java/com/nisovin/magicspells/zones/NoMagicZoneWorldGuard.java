package com.nisovin.magicspells.zones;

import com.nisovin.magicspells.MagicSpells;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;


public class NoMagicZoneWorldGuard extends NoMagicZone {

	private String worldName;
	private String regionName;

	@Override
	public void initialize(ConfigurationSection config) {
		this.worldName = config.getString("world", "");
		this.regionName = config.getString("region", "");
	}

	@Override
	public boolean inZone(Location location) {
		return MagicSpells.getVolatileCodeHandler().getWorldGuardHandler().inZone(location, worldName, regionName);
	}
}
