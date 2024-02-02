package org.starloco.locos.item;

import java.util.Arrays;

// TODO Finish translation
public enum ItemType {
    NECKLACE(1, true),
    BOW(2, true),
    WAND(3, true),
    STAFF(4, true),
    DAGGERS(5, true),
    SWORD(6, true),
    HAMMER(7, true),
    SHOVEL(8, true),
    RING(9, true),
    BELT(10, true),
    BOOTS(11, true),
    POTION(12, true),
    EXP_SCROLL(13, true),
    GIFT(14, true),
    RESOURCE(15),
    HAT(16, true),
    CLOAK(17, true),
    PET(18, true),
    AXE(19, true),
    TOOL(20, true),
    PICKAXE(21, true),
    SCYTHE(22, true),
    DOFUS(23, true),
    QUEST(24, true),
    DOCUMENT(25, true),
    FM_POTION(26),
    TRANSFORM(27, true),
    BOOST_FOOD(28, true),
    BLESSING(29, true),
    CURSE(30, true),
    ROLEPLAY_BUFF(31, true),
    FOLLOWER(32, true),
    BREAD(33, true),
    CEREAL(34),
    FLOWER(35),
    PLANT(36),
    BEER(37, true),
    WOOD(38),
    ORE(39),
    ALLOY(40),
    FISH(41),
    CANDY(42, true),
    POTION_OUBLIE(43, true),
    JOB_POTION(44, true),
    SPELL_POTION(45, true),
    FRUIT(46),
    BONE(47),
    POWDER(48),
    EDIBLE_FISH(49, true),
    PIERRE_PRECIEUSE(50),
    ROUGH_STONE(51),
    FLOUR(52),
    FEATHER(53),
    HAIR(54),
    ETOFFE(55),
    LEATHER(56),
    WOOL(57),
    SEED(58),
    SKIN(59),
    OIL(60),
    PLUSH(61),
    POISSON_VIDE(62),
    MEAT(63),
    VIANDE_CONSERVEE(64),
    QUEUE(65),
    METARIA(66),
    // No 67
    LEGUME(68),
    EDIBLE_EAT(69, true),
    DYE(70),
    EQUIP_ALCHIMIE(71, true),
    PET_EGG(72, true),
    MASTERY(73, true),
    FEE_ARTIFICE(74, true),
    PARCHEMIN_SORT(75, true),
    PARCHEMIN_CARAC(76, true),
    KENNEL_CERTIFICATE(77, true),
    RUNE_FORGEMAGIE(78),
    DRINK(79, true),
    OBJET_MISSION(80),
    BACKPACK(81, true),
    SHIELD(82, true),
    SOUL_STONE(83, true),
    KEY(84),
    FULL_SOUL_STONE(85, true),
    POPO_OUBLI_PERCEP(86, true),
    PARCHO_RECHERCHE(87, true),
    PIERRE_MAGIQUE(88),
    PRESENT(89, true),
    PET_GHOST(90, true),
    DRAGOTURKEY(91, true),
    BOUFTOU(92, true),
    OBJET_ELEVAGE(93, true),
    USABLE_ITEM(94, true),
    PLANK(95),
    BARK(96),
    MOUNT_CERTIFICATE(97, true),
    ROOT(98),
    MOUNT_NET(99, true),
    RESOURCE_BAG(100, true),
    // No 101
    CROSSBOW(102, true),
    PAW(103),
    WING(104),
    EGG(105),
    EAR(106),
    CARAPACE(107),
    BUD(108),
    EYE(109),
    JELLY(110),
    SHELL(111),
    PRISM(112, true),
    OBJET_VIVANT(113, true),
    MAGICAL_WEAPON(114, true),
    SHUSHU_SOUL_FRAGMENT(115, true),
    PET_POTION(116, true),
    COMMON_CARD(119),
    RARE_CARD(120),
    EPIC_CARD(121),
    ULTIMATE_CARD(123),
    BOSS_SOUL_STONE(124),
    ARCHMONSTER_SOUL_STONE(125),
    TONICS(126),
    RESERVED_127(127),
    DOFUS_FRAGMENT(130);

    public static final ItemType[] WEAPONS = {
            SWORD, SHOVEL, HAMMER, AXE, DAGGERS,
            BOW, WAND, STAFF,
            PICKAXE, TOOL, SCYTHE,
            MAGICAL_WEAPON, CROSSBOW
    };

    public final int clientID;
    public final boolean hasStats;

    ItemType(int clientID) {
        this(clientID, false);
    }

    ItemType(int clientID, boolean hasStats) {
        this.clientID = clientID;
        this.hasStats =  hasStats;
    }

    public static ItemType fromClientID(int type) {
        return Arrays.stream(ItemType.values())
            .filter(t -> t.clientID == type)
            .findFirst()
            .orElseThrow(()->  new RuntimeException("unknown item type #"+type));
    }
}
