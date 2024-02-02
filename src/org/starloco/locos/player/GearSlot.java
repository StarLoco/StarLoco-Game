package org.starloco.locos.player;

import org.starloco.locos.item.ItemType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum GearSlot {
    NecklaceSlot(0, ItemType.NECKLACE),
    WeaponSlot(1, ItemType.WEAPONS),
    Ring1Slot(2, ItemType.RING),
    BeltSlot(3, ItemType.BELT),
    Ring2Slot(4, ItemType.RING),
    FeetSlot(5, ItemType.BOOTS),
    HeadSlot(6, ItemType.HAT),
    BackSlot(7, ItemType.CLOAK),
    PetSlot(8, ItemType.PET),
    Dofus1Slot(9, ItemType.DOFUS),
    Dofus2Slot(10, ItemType.DOFUS),
    Dofus3Slot(11, ItemType.DOFUS),
    Dofus4Slot(12, ItemType.DOFUS),
    Dofus5Slot(13, ItemType.DOFUS),
    Dofus6Slot(14, ItemType.DOFUS),
    ShieldSlot(15, ItemType.SHIELD),
    MountSlot(16), // No supported ItemType because the player cannot equip a mount directly
    MutationSlot(20), // FIXME
    RolePlayBuffSlot(21, ItemType.ROLEPLAY_BUFF),
    CurseSlot(22, ItemType.CURSE),
    BlessingSlot(23, ItemType.BLESSING),
    FollowerSlot(24, ItemType.FOLLOWER),
    CandySlot(25, ItemType.CANDY);

    public final int clientSlot;
    public final Set<ItemType> allowedTypes;

    GearSlot(int clientSlot, ItemType... types) {
        this.clientSlot = clientSlot;
        this.allowedTypes = new HashSet<>(Arrays.asList(types));

    }
}
