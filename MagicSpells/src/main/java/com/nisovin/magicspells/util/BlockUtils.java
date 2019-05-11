package com.nisovin.magicspells.util;

import java.util.HashMap;
import java.util.List;

import com.nisovin.magicspells.materials.SpellMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.material.NetherWarts;

import com.nisovin.magicspells.DebugHandler;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;

public class BlockUtils {
	
	private static HashMap<NetherWartsState, Integer> wartStateToInt = new HashMap<>();
	private static HashMap<Integer, NetherWartsState> intToWartState = new HashMap<>();
	
	static {
		wartStateToInt.put(NetherWartsState.SEEDED, 1);
		wartStateToInt.put(NetherWartsState.STAGE_ONE, 2);
		wartStateToInt.put(NetherWartsState.STAGE_TWO, 3);
		wartStateToInt.put(NetherWartsState.RIPE, 4);
		
		intToWartState.put(1, NetherWartsState.SEEDED);
		intToWartState.put(2, NetherWartsState.STAGE_ONE);
		intToWartState.put(3, NetherWartsState.STAGE_TWO);
		intToWartState.put(4, NetherWartsState.RIPE);
	}
	
	public static boolean isTransparent(Spell spell, Block block) {
		return spell.getLosTransparentBlocks().contains(block.getType());
	}
	
	public static Block getTargetBlock(Spell spell, LivingEntity entity, int range) {
		try {
			if (spell != null) return entity.getTargetBlock(spell.getLosTransparentBlocks(), range);
			return entity.getTargetBlock(MagicSpells.getTransparentBlocks(), range);				
		} catch (IllegalStateException e) {
			DebugHandler.debugIllegalState(e);
			return null;
		}
	}
	
	public static List<Block> getLastTwoTargetBlock(Spell spell, LivingEntity entity, int range) {
		try {
			return entity.getLastTwoTargetBlocks(spell.getLosTransparentBlocks(), range);
		} catch (IllegalStateException e) {
			DebugHandler.debugIllegalState(e);
			return null;
		}
	}

	// TODO: Add BlockData support
	public static void setTypeAndData(Block block, Material material, byte data, boolean physics) {
		block.setType(material, physics);
		block.getState().setRawData(data);
		// block.setTypeIdAndData(material.getId(), data, physics);
	}
	
	public static void setBlockFromFallingBlock(Block block, FallingBlock fallingBlock, boolean physics) {
		MagicSpells.getVolatileCodeHandler().setBlockFromFallingBlock(block, fallingBlock, physics);
	}
	
	public static int getWaterLevel(Block block) {
		return block.getData();
	}
	
	public static int getGrowthLevel(Block block) {
		return block.getData();
	}

	// TODO: Add BlockData support
	public static void setGrowthLevel(Block block, int level) {
		block.getState().setRawData((byte)level);
	}
	
	public static boolean growWarts(NetherWarts wart, int stagesToGrow) {
		if (wart.getState() == NetherWartsState.RIPE) return false;
		int state = wartStateToInt.get(wart.getState());
		state= Math.min(state+stagesToGrow, 4);
		wart.setState(intToWartState.get(state));
		return true;
		
	}
	
	public static int getWaterLevel(BlockState blockState) {
		return blockState.getRawData();
	}
	
	public static boolean isPathable(Block block) {
		return isPathable(block.getType());
	}

	public static boolean isPathable(Material material) {
		SpellMaterial mat = SpellMaterial.fromMaterial(material);
		switch (mat) {
			case AIR:
			case OAK_SAPLING:
			case SPRUCE_SAPLING:
			case BIRCH_SAPLING:
			case JUNGLE_SAPLING:
			case ACACIA_SAPLING:
			case DARK_OAK_SAPLING:
			case WATER:
			case POWERED_RAIL:
			case DETECTOR_RAIL:
			case GRASS:
			case FERN:
			case DEAD_BUSH:
			case SEAGRASS:
			case DANDELION:
			case POPPY:
			case BLUE_ORCHID:
			case ALLIUM:
			case AZURE_BLUET:
			case RED_TULIP:
			case ORANGE_TULIP:
			case WHITE_TULIP:
			case PINK_TULIP:
			case OXEYE_DAISY:
			case BROWN_MUSHROOM:
			case RED_MUSHROOM:
			case TORCH:
			case FIRE:
			case REDSTONE_WIRE:
			case SIGN:
			case WALL_SIGN:
			case LEVER:
			case STONE_PRESSURE_PLATE:
			case OAK_PRESSURE_PLATE:
			case SPRUCE_PRESSURE_PLATE:
			case BIRCH_PRESSURE_PLATE:
			case JUNGLE_PRESSURE_PLATE:
			case ACACIA_PRESSURE_PLATE:
			case DARK_OAK_PRESSURE_PLATE:
			case REDSTONE_TORCH:
			case REDSTONE_WALL_TORCH:
			case STONE_BUTTON:
			case SNOW:
			case SUGAR_CANE:
			case VINE:
			case LILY_PAD:
			case NETHER_WART:
			case WHITE_CARPET:
			case ORANGE_CARPET:
			case MAGENTA_CARPET:
			case LIGHT_BLUE_CARPET:
			case YELLOW_CARPET:
			case LIME_CARPET:
			case PINK_CARPET:
			case GRAY_CARPET:
			case LIGHT_GRAY_CARPET:
			case CYAN_CARPET:
			case PURPLE_CARPET:
			case BLUE_CARPET:
			case BROWN_CARPET:
			case GREEN_CARPET:
			case RED_CARPET:
			case BLACK_CARPET:
				return true;
			default:
				return false;
		}
	}

	public static boolean isPlant(Material material) {
		SpellMaterial mat = SpellMaterial.fromMaterial(material);
		switch (mat) {
			case GRASS:
			case FERN:
			case DEAD_BUSH:
			case SEAGRASS:
			case DANDELION:
			case POPPY:
			case BLUE_ORCHID:
			case ALLIUM:
			case AZURE_BLUET:
			case RED_TULIP:
			case ORANGE_TULIP:
			case WHITE_TULIP:
			case PINK_TULIP:
			case OXEYE_DAISY:
				return true;
			default:
				return false;
		}
	}

	public static boolean isSafeToStand(Location location) {
		if (!isPathable(location.getBlock())) return false;
		if (!isPathable(location.add(0, 1, 0).getBlock())) return false;
		return !isPathable(location.subtract(0, 2, 0).getBlock()) || !isPathable(location.subtract(0, 1, 0).getBlock());
	}
	
}
