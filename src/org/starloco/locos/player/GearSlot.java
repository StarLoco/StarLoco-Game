package org.starloco.locos.player;

public enum GearSlot {
    NecklaceSlot(0),
    WeaponSlot(1),
    Ring1Slot(2),
    BeltSlot(3),
    Ring2Slot(4),
    FeetSlot(5),
    HeadSlot(6),
    BackSlot(7),
    PetSlot(8),
    Dofus1Slot(9),
    Dofus2Slot(10),
    Dofus3Slot(11),
    Dofus4Slot(12),
    Dofus5Slot(13),
    Dofus6Slot(14),
    ShieldSlot(15),
    MountSlot(16),
    MutationSlot(20),
    RolePlayBuffSlot(21),
    CurseSlot(22),
    BlessingSlot(23),
    FollowerSlot(24),
    CandySlot(25),
    NotEquipped(-1);

    public final int clientSlot;

    GearSlot(int clientSlot) {
        this.clientSlot = clientSlot;
    }
}
