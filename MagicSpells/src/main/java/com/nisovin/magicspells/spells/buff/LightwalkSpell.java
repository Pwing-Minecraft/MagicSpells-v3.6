package com.nisovin.magicspells.spells.buff;

import java.util.HashMap;
import java.util.Objects;

import com.nisovin.magicspells.materials.SpellMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.MaterialData;

import com.nisovin.magicspells.materials.MagicBlockMaterial;
import com.nisovin.magicspells.materials.MagicMaterial;
import com.nisovin.magicspells.spells.BuffSpell;
import com.nisovin.magicspells.util.MagicConfig;
import com.nisovin.magicspells.util.PlayerNameUtils;
import com.nisovin.magicspells.util.Util;

public class LightwalkSpell extends BuffSpell {
	
	private HashMap<String, Block> lightwalkers;
	private MagicMaterial mat = new MagicBlockMaterial(new MaterialData(Material.GLOWSTONE));

	public LightwalkSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		this.lightwalkers = new HashMap<>();
	}

	@Override
	public boolean castBuff(Player player, float power, String[] args) {
		this.lightwalkers.put(player.getName(), null);
		return true;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		String playerName = p.getName();
		if (this.lightwalkers.containsKey(playerName)) {
			Block oldBlock = this.lightwalkers.get(playerName);
			Block newBlock = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (!Objects.equals(oldBlock, newBlock) && allowedType(newBlock.getType()) && newBlock.getType() != Material.AIR) {
				if (isExpired(p)) {
					turnOff(p);
				} else {
					if (oldBlock != null) Util.restoreFakeBlockChange(p, oldBlock);
					Util.sendFakeBlockChange(p, newBlock, mat);
					this.lightwalkers.put(playerName, newBlock);
					addUse(p);
					chargeUseCost(p);
				}
			}
		}
	}
	
	private boolean allowedType(Material mat) {
		SpellMaterial spellMat = SpellMaterial.fromMaterial(mat);
		switch (spellMat) {
			case DIRT:
			case GRASS_BLOCK:
			case STONE:
			case COBBLESTONE:
			case OAK_LOG:
			case SPRUCE_LOG:
			case BIRCH_LOG:
			case JUNGLE_LOG:
			case ACACIA_LOG:
			case DARK_OAK_LOG:
			case OAK_PLANKS:
			case SPRUCE_PLANKS:
			case BIRCH_PLANKS:
			case JUNGLE_PLANKS:
			case ACACIA_PLANKS:
			case DARK_OAK_PLANKS:
			case NETHERRACK:
			case SOUL_SAND:
			case SAND:
			case SANDSTONE:
			case GLASS:
			case WHITE_WOOL:
			case ORANGE_WOOL:
			case MAGENTA_WOOL:
			case LIGHT_BLUE_WOOL:
			case YELLOW_WOOL:
			case LIME_WOOL:
			case PINK_WOOL:
			case GRAY_WOOL:
			case LIGHT_GRAY_WOOL:
			case CYAN_WOOL:
			case BLUE_WOOL:
			case BROWN_WOOL:
			case GREEN_WOOL:
			case RED_WOOL:
			case BLACK_WOOL:
			case SMOOTH_STONE:
			case BRICKS:
			case OBSIDIAN:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public void turnOffBuff(Player player) {
		Block b = this.lightwalkers.remove(player.getName());
		if (b == null) return;
		Util.restoreFakeBlockChange(player, b);
	}

	@Override
	protected void turnOff() {
		for (String s : this.lightwalkers.keySet()) {
			Player p = PlayerNameUtils.getPlayer(s);
			if (p == null) continue;
			
			Block b = this.lightwalkers.get(s);
			if (b == null) continue;
			
			Util.restoreFakeBlockChange(p, b);
		}
		this.lightwalkers.clear();
	}

	@Override
	public boolean isActive(Player player) {
		return this.lightwalkers.containsKey(player.getName());
	}

}
