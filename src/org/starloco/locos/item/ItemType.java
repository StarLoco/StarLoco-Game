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
    POTION_METIER(44, true),
    POTION_SORT(45, true),
    FRUIT(46),
    OS(47),
    POUDRE(48),
    EDIBLE_FISH(49, true),
    PIERRE_PRECIEUSE(50),
    PIERRE_BRUTE(51),
    FARINE(52),
    PLUME(53),
    POIL(54),
    ETOFFE(55),
    CUIR(56),
    LAINE(57),
    GRAINE(58),
    PEAU(59),
    HUILE(60),
    PELUCHE(61),
    POISSON_VIDE(62),
    VIANDE(63),
    VIANDE_CONSERVEE(64),
    QUEUE(65),
    METARIA(66),
    // No 67
    LEGUME(68),
    EDIBLE_EAT(69, true),
    TEINTURE(70),
    EQUIP_ALCHIMIE(71, true),
    OEUF_FAMILIER(72, true),
    MAITRISE(73, true),
    FEE_ARTIFICE(74, true),
    PARCHEMIN_SORT(75, true),
    PARCHEMIN_CARAC(76, true),
    KENNEL_CERTIFICATE(77, true),
    RUNE_FORGEMAGIE(78),
    BOISSON(79, true),
    OBJET_MISSION(80),
    SAC_DOS(81, true),
    SHIELD(82, true),
    SOUL_STONE(83, true),
    CLEFS(84),
    FULL_SOUL_STONE(85, true),
    POPO_OUBLI_PERCEP(86, true),
    PARCHO_RECHERCHE(87, true),
    PIERRE_MAGIQUE(88),
    CADEAUX(89, true),
    FANTOME_FAMILIER(90, true),
    DRAGODINDE(91, true),
    BOUFTOU(92, true),
    OBJET_ELEVAGE(93, true),
    OBJET_UTILISABLE(94, true),
    PLANCHE(95),
    ECORCE(96),
    MOUNT_CERTIFICATE(97, true),
    RACINE(98),
    FILET_CAPTURE(99, true),
    SAC_RESSOURCE(100, true),
    // No 101
    CROSSBOW(102, true),
    PATTE(103),
    AILE(104),
    OEUF(105),
    OREILLE(106),
    CARAPACE(107),
    BOURGEON(108),
    OEIL(109),
    GELEE(110),
    COQUILLE(111),
    PRISM(112, true),
    OBJET_VIVANT(113, true),
    MAGICAL_WEAPON(114, true),
    FRAGM_AME_SHUSHU(115, true),
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
