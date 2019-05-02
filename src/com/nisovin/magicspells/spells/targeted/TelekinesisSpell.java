package com.nisovin.magicspells.spells.targeted;

import java.util.HashSet;

import com.nisovin.magicspells.materials.SpellMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.events.MagicSpellsPlayerInteractEvent;
import com.nisovin.magicspells.events.SpellTargetLocationEvent;
import com.nisovin.magicspells.spells.TargetedLocationSpell;
import com.nisovin.magicspells.spells.TargetedSpell;
import com.nisovin.magicspells.util.compat.EventUtil;
import com.nisovin.magicspells.util.HandHandler;
import com.nisovin.magicspells.util.MagicConfig;

public class TelekinesisSpell extends TargetedSpell implements TargetedLocationSpell {
	
	private boolean checkPlugins;
	
	public TelekinesisSpell(MagicConfig config, String spellName) {
		super(config, spellName);
		
		checkPlugins = getConfigBoolean("check-plugins", true);
		
		losTransparentBlocks = new HashSet<>(losTransparentBlocks);
		losTransparentBlocks.remove(Material.LEVER);
		losTransparentBlocks.remove(SpellMaterial.STONE_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.OAK_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.SPRUCE_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.BIRCH_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.JUNGLE_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.ACACIA_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.DARK_OAK_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.parseMaterial());
		losTransparentBlocks.remove(Material.STONE_BUTTON);
		losTransparentBlocks.remove(SpellMaterial.OAK_BUTTON.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.SPRUCE_BUTTON.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.BIRCH_BUTTON.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.JUNGLE_BUTTON.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.ACACIA_BUTTON.parseMaterial());
		losTransparentBlocks.remove(SpellMaterial.DARK_OAK_BUTTON.parseMaterial());
	}
	
	@Override
	public PostCastAction castSpell(Player player, SpellCastState state, float power, String[] args) {
		if (state == SpellCastState.NORMAL) {
			Block target = getTargetedBlock(player, power);
			if (target == null) {
				// Fail
				return noTarget(player);
			}
			
			// Run target event
			SpellTargetLocationEvent event = new SpellTargetLocationEvent(this, player, target.getLocation(), power);
			EventUtil.call(event);
			if (event.isCancelled()) return noTarget(player);
			
			target = event.getTargetLocation().getBlock();
			
			// Run effect
			boolean activated = activate(player, target);
			if (!activated) return noTarget(player);
			
			playSpellEffects(player, target.getLocation());
		}
		return PostCastAction.HANDLE_NORMALLY;
	}
	
	private boolean activate(Player caster, Block target) {
		SpellMaterial targetType = SpellMaterial.fromMaterial(target.getType());
		switch (targetType) {
			case LEVER:
			case STONE_BUTTON:
			case OAK_BUTTON:
			case SPRUCE_BUTTON:
			case BIRCH_BUTTON:
			case JUNGLE_BUTTON:
			case ACACIA_BUTTON:
			case DARK_OAK_BUTTON:
				if (checkPlugins(caster, target)) {
					MagicSpells.getVolatileCodeHandler().toggleLeverOrButton(target);
					return true;
				}
				break;
			case OAK_PRESSURE_PLATE:
			case SPRUCE_PRESSURE_PLATE:
			case BIRCH_PRESSURE_PLATE:
			case JUNGLE_PRESSURE_PLATE:
			case ACACIA_PRESSURE_PLATE:
			case DARK_OAK_PRESSURE_PLATE:
			case STONE_PRESSURE_PLATE:
			case HEAVY_WEIGHTED_PRESSURE_PLATE:
			case LIGHT_WEIGHTED_PRESSURE_PLATE:
				if (checkPlugins(caster, target)) {
					MagicSpells.getVolatileCodeHandler().pressPressurePlate(target);
					return true;
				}
				break;
			default:
				break;
		}

		return false;
	}
	
	private boolean checkPlugins(Player caster, Block target) {
		if (!checkPlugins) return true;
		MagicSpellsPlayerInteractEvent event = new MagicSpellsPlayerInteractEvent(caster, Action.RIGHT_CLICK_BLOCK, HandHandler.getItemInMainHand(caster), target, BlockFace.SELF);
		EventUtil.call(event);
		return event.useInteractedBlock() != Result.DENY;
	}

	@Override
	public boolean castAtLocation(Player caster, Location target, float power) {
		boolean activated = activate(caster, target.getBlock());
		if (activated) playSpellEffects(caster, target);
		return activated;
	}

	@Override
	public boolean castAtLocation(Location target, float power) {
		return false;
	}
	
}
