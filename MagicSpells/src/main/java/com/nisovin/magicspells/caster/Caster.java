package com.nisovin.magicspells.caster;

import com.nisovin.magicspells.Spell;
import com.nisovin.magicspells.util.SpellReagents;
import org.bukkit.entity.Entity;

public interface Caster {

    boolean canLearn(Spell spell);

    boolean canCast(Spell spell);

    boolean canTeach(Spell spell);

    boolean hasAdvancedPerm(String spell);

    boolean hasReagents(SpellReagents reagents);

    void removeReagents(SpellReagents reagents);

    boolean isValid();
}
