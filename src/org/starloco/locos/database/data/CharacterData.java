package org.starloco.locos.database.data;

import org.starloco.locos.database.DynamicData;

import java.util.HashSet;
import java.util.Set;

public class CharacterData implements DynamicData {
    public final long id;
    private String name;
    private int breed;
    private int color1;
    private int color2;
    private int color3;
    public final CharacterAttributesWithPoints baseAttributes = new CharacterAttributesWithPoints();

    // Not strictly needed for ankalike. But it's a good to have to make respecs easier
    public final CharacterAttributes scrollAttributes = new CharacterAttributes();

    public final CharacterGear gear = new CharacterGear();
    public final CharacterSpell spells = new CharacterSpell();
    // public final CharacterFaction factionMember;
    // public final CharacterGuild guildMember;
    public final CharacterSettings settings = new CharacterSettings(); // shortcuts, channels
    // public final CharacterMerchant merchant;
    public final Set<Integer> zaaps = new HashSet<>();
    public final Set<Integer> emotes = new HashSet<>();

    public CharacterData(long id) {
        this.id = id;
    }

    public String GUID() {
        return String.valueOf(this.id);
    }
}