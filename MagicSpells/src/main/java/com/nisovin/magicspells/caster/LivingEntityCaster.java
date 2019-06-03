package com.nisovin.magicspells.caster;

import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Perm;
import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.util.HandHandler;
import com.nisovin.magicspells.util.SpellReagents;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LivingEntityCaster implements Caster {

    private LivingEntity entity;

    private LivingEntityCaster(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canLearn(Spell spell) {
        if (spell.isHelperSpell()) {
            MagicSpells.debug("Cannot learn " + spell.getName() + " because it is a helper spell");
            return false;
        }

        if (spell.getPrerequisites() != null && !spell.getPrerequisites().isEmpty()) {
            for (String spellName : spell.getPrerequisites()) {
                Spell sp = MagicSpells.getSpellByInGameName(spellName);
                if (sp == null) {
                    MagicSpells.debug("Cannot learn " + spell.getName() + " because the prerequisite of " + spellName + " has not been satisfied");
                    return false;
                }
            }
        }

        // TODO: Find a way to add this in...
        /*
        if (spell.getXpRequired() != null && !spell.getXpRequired().isEmpty()) {
            MagicXpHandler handler = MagicSpells.getMagicXpHandler();
            if (handler != null) {
                for (String school : spell.getXpRequired().keySet()) {
                    if (handler.getXp(entity, school) < spell.getXpRequired().get(school)) {
                        MagicSpells.debug("Cannot learn " + spell.getName() + " because the target does not have enough magic xp");
                        return false;
                    }
                }
            }
        }
        */

        // TODO: Add permission support for entities learning?
        // MagicSpells.debug("Checking learn permissions for " + entity.getName());
        return true;
    }

    @Override
    public boolean canCast(Spell spell) {
        if (spell.isHelperSpell())
            return true;

        if (MagicSpells.ignoreEntityCastPerms())
            return true;

        return Perm.CAST.has(entity, spell);
    }

    @Override
    public boolean canTeach(Spell spell) {
        if (spell.isHelperSpell())
            return false;

        // TODO: Add permission support for entities teaching?
        return true;
    }

    @Override
    public boolean hasAdvancedPerm(String spell) {
        return entity.hasPermission(Perm.ADVANCED.getNode() + spell);
    }

    @Override
    public boolean hasReagents(SpellReagents reagents) {
        if (Perm.NOREAGENTS.has(entity))
            return true;

        if (reagents.getHealth() > 0 && entity.getHealth() <= reagents.getHealth())
            return false;

        if (reagents.getMana() > 0 && (MagicSpells.getManaHandler() == null || !MagicSpells.getManaHandler().hasMana(entity, reagents.getMana())))
            return false;

        if (reagents.getDurability() > 0) {
            ItemStack inHand = HandHandler.getItemInMainHand(entity.getEquipment());
            if (inHand == null || inHand.getDurability() >= inHand.getType().getMaxDurability())
                return false;
        }

        if (reagents.getItemsAsArray() != null) {
            List<ItemStack> items = new ArrayList<ItemStack>();
            items.addAll(Arrays.asList(entity.getEquipment().getArmorContents()));
            items.add(HandHandler.getItemInMainHand(entity.getEquipment()));
            items.add(HandHandler.getItemInOffHand(entity.getEquipment()));

            for (ItemStack item : reagents.getItemsAsArray()) {
                if (item != null && !items.contains(item))
                    return false;
            }
        }

        // TODO: Find a way to add this in...
        /*
        if (reagents.getVariables() != null) {
            VariableManager varMan = MagicSpells.getVariableManager();
            if (varMan == null)
                return false;

            for (Map.Entry<String, Double> var : reagents.getVariables().entrySet()) {
                double val = var.getValue();
                if (val > 0 && varMan.getValue(var.getKey(), entity) < val)
                    return false;
            }
        }
        */

        return true;
    }

    @Override
    public void removeReagents(SpellReagents reagents) {
        if (Perm.NOREAGENTS.has(entity))
            return;

        if (reagents.getItemsAsArray() != null) {
            ItemStack[] contents = entity.getEquipment().getArmorContents();
            for (ItemStack reagent : reagents.getItems()) {
                int amount = reagent.getAmount();
                for (int i = 0; i < contents.length; i++) {
                    ItemStack item = contents[i];
                    if (item != null && reagent.isSimilar(item)) {
                        if (item.getAmount() > reagent.getAmount()) {
                            item.setAmount(item.getAmount() - amount);
                            amount = 0;
                            break;
                        } else if (item.getAmount() == amount) {
                            item = null;
                            amount = 0;
                            break;
                        } else {
                            amount -= item.getAmount();
                            item = null;
                        }
                    }
                }

                if (amount == 0) {
                    entity.getEquipment().setArmorContents(contents);
                }
            }
        }

        if (reagents.getHealth() != 0) {
            double health = entity.getHealth() - reagents.getHealth();
            if (health < 0)
                health = 0;
            if (health > entity.getMaxHealth())
                health = entity.getMaxHealth();

            entity.setHealth(health);
        }

        if (reagents.getMana() != 0) {
            MagicSpells.getManaHandler().addMana(entity, -reagents.getMana(), ManaChangeReason.SPELL_COST);
        }

        if (reagents.getDurability() != 0) {
            ItemStack inHand = HandHandler.getItemInMainHand(entity.getEquipment());
            if (inHand != null && inHand.getType().getMaxDurability() > 0) {
                short newDura = (short) (inHand.getDurability() + reagents.getDurability());
                if (newDura < 0) newDura = 0;
                if (newDura >= inHand.getType().getMaxDurability()) {
                    HandHandler.setItemInMainHand(entity.getEquipment(), null);
                } else {
                    inHand.setDurability(newDura);
                    HandHandler.setItemInMainHand(entity.getEquipment(), inHand);
                }
            }
        }

        // TODO: Find a way to add this in...
        /*
        if (reagents.getVariables() != null) {
            VariableManager varMan = MagicSpells.getVariableManager();
            if (varMan != null) {
                for (Map.Entry<String, Double> var : reagents.getVariables().entrySet()) {
                    varMan.modify(var.getKey(), entity, -var.getValue());
                }
            }
        }
        */
    }

    @Override
    public boolean isValid() {
        return entity.isValid();
    }

    public Entity getEntity() {
        return entity;
    }
}