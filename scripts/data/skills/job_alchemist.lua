local jobID = AlchemistJob
local toolIDs = {1473}

--FIXME timing / Reward
local gatherSkills = {
    {id=68,  obj=Objects.Flax,            minLvl=0,   itemID=421,  xp=10, respawn={6000, 10000} },
    {id=69,  obj=Objects.Hemp,            minLvl=10,  itemID=428,  xp=15, respawn={6000, 10000} },
    {id=71,  obj=Objects.FiveLeafClover,  minLvl=20,  itemID=395,  xp=20, respawn={6000, 10000} },
    {id=72,  obj=Objects.WildMint,        minLvl=30,  itemID=380,  xp=25, respawn={6000, 10000} },
    {id=73,  obj=Objects.FreyesqueOrchid, minLvl=40,  itemID=593,  xp=30, respawn={6000, 10000} },
    {id=74,  obj=Objects.Edelweiss,       minLvl=50,  itemID=594,  xp=35, respawn={6000, 10000} },
    {id=160, obj=Objects.Pandkin,         minLvl=50,  itemID=7059, xp=35, respawn={6000, 10000} },
}

registerGatherJobSkills(jobID, {toolIDs=toolIDs}, gatherSkills)

-- Craft for Prepare a potion
local sk23Crafts = {
    -- Superior Health Flask
    {item=282, ingredients={ {395, 4}, {594, 2}, {311, 1}, {1459, 1} } },
    -- Health Flask
    {item=283, ingredients={ {380, 2}, {593, 2}, {311, 1}, {1459, 1} } },
    -- Baker's Yeast
    {item=286, ingredients={ {311, 1}, {519, 1} } },
    -- Recall Potion
    {item=548, ingredients={ {311, 1}, {395, 1} } },
    -- Ghetto Raid Potion
    {item=580, ingredients={ {380, 3}, {793, 1}, {395, 3} } },
    -- Potion of Old Age
    {item=793, ingredients={ {385, 2}, {425, 2} } },
    -- Mini Healing Potion
    {item=1182, ingredients={ {421, 3}, {311, 1} } },
    -- Superior Mini Healing Potion
    {item=1183, ingredients={ {311, 1}, {421, 2}, {428, 2} } },
    -- Spark Potion
    {item=1333, ingredients={ {1526, 5}, {1463, 1} } },
    -- Drizzle Potion
    {item=1335, ingredients={ {1527, 5}, {1463, 1} } },
    -- Draught Potion
    {item=1337, ingredients={ {1463, 1}, {1529, 5} } },
    -- Tremor Potion
    {item=1338, ingredients={ {1528, 5}, {1463, 1} } },
    -- Landslide Potion
    {item=1340, ingredients={ {1534, 5}, {1463, 1} } },
    -- Shower Potion
    {item=1341, ingredients={ {1531, 5}, {1463, 1} } },
    -- Gust Potion
    {item=1342, ingredients={ {1532, 5}, {1463, 1} } },
    -- Blazing Fire Potion
    {item=1343, ingredients={ {1533, 5}, {1463, 1} } },
    -- Fire Potion
    {item=1345, ingredients={ {1535, 5}, {1463, 1} } },
    -- Tsunami Potion
    {item=1346, ingredients={ {1536, 5}, {1463, 1} } },
    -- Hurricane Potion
    {item=1347, ingredients={ {1463, 1}, {1537, 5} } },
    -- Earthquake Potion
    {item=1348, ingredients={ {1538, 5}, {1463, 1} } },
    -- Fairy Water
    {item=1405, ingredients={ {311, 1}, {1460, 1}, {421, 8}, {428, 8}, {593, 4}, {594, 2} } },
    -- Unikron Blood
    {item=1406, ingredients={ {594, 4}, {311, 1}, {1460, 1}, {380, 8}, {395, 8}, {593, 6} } },
    -- Scalding Poison
    {item=1461, ingredients={ {311, 1}, {1468, 1}, {365, 10} } },
    -- Vampiric Liquid
    {item=1462, ingredients={ {311, 1}, {752, 1}, {310, 4}, {1468, 1} } },
    -- Unlearning Potion for Profession: Dagger Smith
    {item=1542, ingredients={ {793, 2}, {495, 1} } },
    -- Unlearning Potion for Profession: Lumberjack
    {item=1587, ingredients={ {793, 2}, {454, 1} } },
    -- Unlearning Potion for Profession: Sword Smith
    {item=1588, ingredients={ {793, 2}, {494, 1} } },
    -- Unlearning Potion for Profession: Bow Carver
    {item=1589, ingredients={ {793, 2}, {500, 1} } },
    -- Unlearning Potion for Profession: Hammer Smith
    {item=1590, ingredients={ {793, 2}, {493, 1} } },
    -- Unlearning Potion for Profession: Shoemaker
    {item=1591, ingredients={ {793, 2}, {579, 1} } },
    -- Unlearning Potion for Profession: Jeweller
    {item=1592, ingredients={ {793, 2}, {491, 1} } },
    -- Unlearning Potion for Profession: Staff Carver
    {item=1593, ingredients={ {793, 2}, {498, 1} } },
    -- Unlearning Potion for Profession: Wand Carver
    {item=1594, ingredients={ {793, 2}, {499, 1} } },
    -- Unlearning Potion for Profession: Shovel Smith
    {item=1595, ingredients={ {793, 2}, {496, 1} } },
    -- Unlearning Potion for Profession: Miner
    {item=1596, ingredients={ {793, 2}, {497, 1} } },
    -- Unlearning Potion for Profession: Baker
    {item=1597, ingredients={ {793, 2}, {492, 1} } },
    -- Unlearning Potion for Profession: Alchemist
    {item=1598, ingredients={ {793, 2}, {1473, 1} } },
    -- Unlearning Potion for Profession: Tailor
    {item=1599, ingredients={ {793, 2}, {951, 1} } },
    -- Unlearning Potion for Profession: Farmer
    {item=1600, ingredients={ {793, 2}, {577, 1} } },
    -- Unlearning Potion for Profession: Axe Smith
    {item=1601, ingredients={ {922, 1}, {793, 2} } },
    -- Unlearning Potion for Profession: Dagger Smithmagus
    {item=1642, ingredients={ {793, 2}, {1520, 1} } },
    -- Unlearning Potion for Profession: Axe Smithmagus
    {item=1643, ingredients={ {793, 2}, {1562, 1} } },
    -- Unlearning Potion for Profession: Hammer Smithmagus
    {item=1644, ingredients={ {1561, 1}, {793, 2} } },
    -- Unlearning Potion for Profession: Sword Smithmagus
    {item=1645, ingredients={ {793, 2}, {1539, 1} } },
    -- Unlearning Potion for Profession: Shovel Smithmagus
    {item=1646, ingredients={ {793, 2}, {1560, 1} } },
    -- Unlearning Potion for Profession: Bow Carvemage
    {item=1647, ingredients={ {1563, 1}, {793, 2} } },
    -- Unlearning Potion for Profession: Wand Carvemage
    {item=1648, ingredients={ {793, 2}, {1564, 1} } },
    -- Unlearning Potion for Profession: Staff Carvemage
    {item=1649, ingredients={ {793, 2}, {1565, 1} } },
    -- Magic Redness Dye
    {item=1686, ingredients={ {381, 10}, {368, 1} } },
    -- Magic Greenish Dye
    {item=1687, ingredients={ {380, 20}, {364, 20} } },
    -- Magic Bluish Dye
    {item=1688, ingredients={ {362, 20}, {757, 1} } },
    -- Magic Orange Dye
    {item=1689, ingredients={ {363, 20}, {311, 10} } },
    -- Magic Dark Dye
    {item=1692, ingredients={ {365, 10}, {307, 10} } },
    -- Pahoa Raid Potion
    {item=1712, ingredients={ {380, 5}, {395, 5}, {593, 3}, {793, 2} } },
    -- Raid Boole Potion
    {item=1713, ingredients={ {793, 3}, {594, 3}, {395, 10}, {380, 10}, {593, 5} } },
    -- Phoenix Blood
    {item=1741, ingredients={ {395, 1}, {793, 1}, {580, 1}, {1405, 1}, {7018, 1}, {7059, 1}, {1183, 1} } },
    -- Great Phoenix Blood
    {item=1742, ingredients={ {7018, 10}, {1713, 1}, {793, 1}, {580, 1}, {1405, 1}, {1183, 1}, {395, 1}, {7059, 10} } },
    -- Unlearning Potion for Profession: Fisherman
    {item=2183, ingredients={ {793, 2}, {596, 1} } },
    -- Unlearning Potion for Profession: Fishmonger
    {item=2184, ingredients={ {793, 2}, {1946, 1} } },
    -- Unlearning Potion for Profession: Hunter
    {item=2185, ingredients={ {793, 2}, {1934, 1} } },
    -- Unlearning Potion for Profession: Butcher
    {item=2186, ingredients={ {793, 2}, {1945, 1} } },
    -- Brakmarian Intercity-Express Potion
    {item=6964, ingredients={ {7047, 1}, {7018, 1} } },
    -- Bontarian Intercity-Express Potion
    {item=6965, ingredients={ {7045, 1}, {7018, 1} } },
    -- Forgetfulness Potion for Perceptors: Aqueous Armour
    {item=7314, ingredients={ {2549, 1}, {2492, 1}, {1575, 1} } },
    -- Forgetfulness Potion for Perceptors: Glowing Armour
    {item=7315, ingredients={ {1575, 1}, {7285, 1}, {2448, 1} } },
    -- Forgetfulness Potion for Perceptors: Earth Armour
    {item=7316, ingredients={ {2491, 1}, {1575, 1}, {1890, 1} } },
    -- Forgetfulness Potion for Perceptors: Wind Armour
    {item=7317, ingredients={ {448, 1}, {305, 1}, {1575, 1} } },
    -- Forgetfulness Potion for Perceptors: Flame
    {item=7318, ingredients={ {1575, 1}, {371, 1}, {2150, 1} } },
    -- Forgetfulness Potion for Perceptors: Cyclone
    {item=7319, ingredients={ {1575, 1}, {1002, 1}, {2321, 1} } },
    -- Forgetfulness Potion for Perceptors: Wave
    {item=7320, ingredients={ {7260, 1}, {1575, 1}, {375, 1} } },
    -- Forgetfulness Potion for Perceptors: Rock
    {item=7321, ingredients={ {3208, 1}, {440, 1}, {1575, 1} } },
    -- Forgetfulness Potion for Perceptors: Healing Word
    {item=7322, ingredients={ {2528, 1}, {1575, 1}, {2248, 1} } },
    -- Forgetfulness Potion for Perceptors: Unbewitchment
    {item=7323, ingredients={ {1575, 1}, {373, 1}, {2584, 1} } },
    -- Forgetfulness Potion for Perceptors: Mass Compulsion
    {item=7324, ingredients={ {310, 1}, {2322, 1}, {1575, 1} } },
    -- Forgetfulness Potion for Perceptors: Destabilization
    {item=7325, ingredients={ {2662, 1}, {1575, 1}, {2336, 1} } },
    -- Forgetfulness Potion for Perceptors: Pods
    {item=7326, ingredients={ {423, 1}, {793, 1}, {350, 1}, {1575, 1}, {594, 1}, {472, 1} } },
    -- Forgetfulness Potion for Perceptors: Number of Perceptors
    {item=7327, ingredients={ {793, 1}, {547, 1}, {1575, 1} } },
    -- Forgetfulness Potion for Perceptors: Prospecting
    {item=7328, ingredients={ {809, 1}, {793, 1}, {1575, 1} } },
    -- Forgetfulness Potion for Perceptors: Experience Tax
    {item=7329, ingredients={ {793, 1}, {678, 1}, {1575, 1} } },
    -- Unlearning Potion for Profession: Shield Smith
    {item=7421, ingredients={ {793, 2}, {7098, 1} } },
    -- Small Mass Healing Potion
    {item=7498, ingredients={ {2315, 1}, {2504, 1}, {1405, 1}, {426, 1}, {417, 1} } },
    -- Unlearning Potion for Profession: Costumagus
    {item=7505, ingredients={ {793, 2}, {7494, 1} } },
    -- Unlearning Potion for Profession: Jewelmagus
    {item=7506, ingredients={ {793, 2}, {7493, 1} } },
    -- Unlearning Potion for Profession: Shoemagus
    {item=7507, ingredients={ {793, 2}, {7495, 1} } },
    -- Memory Potion
    {item=7652, ingredients={ {385, 1}, {7059, 1} } },
    -- Unlearning Potion for Profession: Handyman
    {item=8106, ingredients={ {793, 2}, {7650, 1} } },
    -- Guild House Potion
    {item=8883, ingredients={ {1770, 1}, {2542, 1}, {1775, 1}, {1777, 1}, {1773, 1} } },
    -- Bulbish Potion
    {item=8913, ingredients={ {7904, 1}, {7068, 1}, {7903, 1}, {8750, 1}, {8752, 1}, {8749, 1}, {8751, 1} } },
    -- Guild Paddock Potion
    {item=9035, ingredients={ {1776, 1}, {1778, 1}, {1774, 1}, {2542, 1}, {1772, 1} } },
    -- Small Attack Potion
    {item=9038, ingredients={ {439, 1}, {9382, 1} } },
    -- Average Attack Potion
    {item=9040, ingredients={ {2302, 1}, {9038, 1}, {2336, 1} } },
    -- Large Attack Potion
    {item=9041, ingredients={ {2557, 1}, {9040, 1}, {426, 1}, {9383, 1} } },
    -- Total Camouflage Potion
    {item=9869, ingredients={ {430, 1}, {8362, 1}, {9888, 1}, {1652, 1} } },
    -- Trashouflage Potion
    {item=9870, ingredients={ {1893, 1}, {7030, 1} } },
    -- Barrouflage Potion
    {item=9882, ingredients={ {9381, 1}, {373, 1} } },
    -- Tumbouflage Potion
    {item=9883, ingredients={ {371, 1}, {2509, 1} } },
    -- Rockouflage Potion
    {item=9884, ingredients={ {2466, 1}, {2150, 1} } },
    -- Stooflage Potion
    {item=9885, ingredients={ {9383, 1}, {375, 1} } },
    -- Logouflage Potion
    {item=9886, ingredients={ {440, 1}, {2665, 1} } },
    -- Plantouflage Potion
    {item=9887, ingredients={ {6736, 1}, {417, 1} } },
    -- Immobile Camouflage Potion
    {item=9888, ingredients={ {7285, 1}, {2528, 5}, {9383, 3} } },
    -- Liberation Potion
    {item=9897, ingredients={ {6736, 5}, {2492, 1}, {1890, 5} } },
    -- Mass Healing Potion
    {item=9898, ingredients={ {7498, 1}, {2302, 1}, {9381, 1}, {305, 1}, {2501, 1}, {9383, 1} } },
    -- Great Mass Healing Potion
    {item=9899, ingredients={ {2455, 1}, {2322, 1}, {8362, 1}, {375, 1}, {9898, 1}, {9382, 1}, {2466, 1} } },
    -- Incredible Mass Healing Potion
    {item=9900, ingredients={ {2497, 1}, {7293, 1}, {2301, 1}, {2490, 1}, {2499, 1}, {2248, 1}, {2528, 1}, {9899, 1} } },
    -- Pandawa Elixir
    {item=10208, ingredients={ {1770, 5}, {1775, 5}, {6736, 1}, {1773, 5}, {1777, 5} } },
    -- Sacrier Elixir
    {item=10209, ingredients={ {1770, 5}, {1777, 5}, {1773, 5}, {1775, 5}, {2455, 1} } },
    -- Eniripsa Elixir
    {item=10210, ingredients={ {1775, 5}, {1770, 5}, {2491, 1}, {1773, 5}, {1777, 5} } },
    -- Sadida Elixir
    {item=10211, ingredients={ {1775, 5}, {1770, 5}, {1773, 5}, {305, 1}, {1777, 5} } },
    -- Cra Elixir
    {item=10212, ingredients={ {1770, 5}, {1775, 5}, {1777, 5}, {1773, 5}, {2516, 1} } },
    -- Iop Elixir
    {item=10213, ingredients={ {1770, 5}, {1773, 5}, {1775, 5}, {1777, 5}, {2448, 1} } },
    -- Ecaflip Elixir
    {item=10214, ingredients={ {1773, 5}, {1770, 5}, {1775, 5}, {1777, 5}, {2528, 1} } },
    -- Xelor Elixir
    {item=10215, ingredients={ {1777, 5}, {1773, 5}, {1775, 5}, {1770, 5}, {388, 1} } },
    -- Feca Elixir
    {item=10216, ingredients={ {1770, 5}, {1773, 5}, {1777, 5}, {1775, 5}, {373, 1} } },
    -- Enutrof Elixir
    {item=10217, ingredients={ {417, 1}, {1773, 5}, {1775, 5}, {1770, 5}, {1777, 5} } },
    -- Osamodas Elixir
    {item=10218, ingredients={ {1773, 5}, {1770, 5}, {1775, 5}, {1777, 5}, {310, 1} } },
    -- Sram Elixir
    {item=10219, ingredients={ {1773, 5}, {1775, 5}, {2322, 1}, {1777, 5}, {1770, 5} } },
    -- Enhancement Potion: Atooin
    {item=10750, ingredients={ {7663, 5}, {750, 5}, {10716, 1}, {10715, 1}, {9941, 1}, {1953, 100}, {1826, 100}, {2636, 25} } },
    -- Enhancement Potion: Pandawa Cub
    {item=10751, ingredients={ {10715, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10717, 1}, {9941, 1} } },
    -- Enhancement Potion: Bilby
    {item=10752, ingredients={ {7663, 5}, {750, 5}, {10715, 1}, {9941, 1}, {10718, 1}, {1826, 100}, {1953, 100}, {2636, 25} } },
    -- Enhancement Potion: Bwak
    {item=10753, ingredients={ {10719, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {9941, 1}, {10715, 1} } },
    -- Enhancement Potion: Bworky
    {item=10754, ingredients={ {7663, 5}, {10715, 1}, {9941, 1}, {10720, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {750, 5} } },
    -- Enhancement Potion: Bow Meow
    {item=10755, ingredients={ {10715, 1}, {10721, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {9941, 1} } },
    -- Enhancement Potion: Angora Bow Meow
    {item=10756, ingredients={ {10715, 1}, {10722, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1} } },
    -- Enhancement Potion: Tabby Bow Meow
    {item=10757, ingredients={ {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10715, 1}, {9941, 1}, {10723, 1} } },
    -- Enhancement Potion: Crocodyl
    {item=10758, ingredients={ {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10724, 1}, {9941, 1}, {10715, 1}, {1826, 100} } },
    -- Enhancement Potion: Mischievous Squirrel
    {item=10759, ingredients={ {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1}, {10725, 1}, {10715, 1} } },
    -- Enhancement Potion: Fëanor
    {item=10760, ingredients={ {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1}, {10715, 1}, {10726, 1} } },
    -- Enhancement Potion: Bloody Koalak
    {item=10761, ingredients={ {10727, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1}, {10715, 1} } },
    -- Enhancement Potion: Quaquack
    {item=10762, ingredients={ {750, 5}, {7663, 5}, {10728, 1}, {9941, 1}, {10715, 1}, {1953, 100}, {1826, 100}, {2636, 25} } },
    -- Enhancement Potion: Leopardo
    {item=10763, ingredients={ {7663, 5}, {750, 5}, {9941, 1}, {10729, 1}, {10715, 1}, {1826, 100}, {1953, 100}, {2636, 25} } },
    -- Enhancement Potion: Mini Wa
    {item=10764, ingredients={ {1826, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1}, {10715, 1}, {10730, 1}, {1953, 100} } },
    -- Enhancement Potion: Nomoon
    {item=10765, ingredients={ {7663, 5}, {750, 5}, {10715, 1}, {10731, 1}, {9941, 1}, {1953, 100}, {1826, 100}, {2636, 25} } },
    -- Enhancement Potion: Peki
    {item=10766, ingredients={ {750, 5}, {9941, 1}, {10715, 1}, {10732, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5} } },
    -- Enhancement Potion: Little White Bow Meow
    {item=10767, ingredients={ {7663, 5}, {10715, 1}, {9941, 1}, {10733, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {750, 5} } },
    -- Enhancement Potion: Little Black Bow Wow
    {item=10768, ingredients={ {2636, 25}, {7663, 5}, {750, 5}, {9941, 1}, {10715, 1}, {10734, 1}, {1826, 100}, {1953, 100} } },
    -- Enhancement Potion: Piwin
    {item=10769, ingredients={ {10715, 1}, {10735, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1} } },
    -- Enhancement Potion: Ross
    {item=10770, ingredients={ {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10736, 1}, {9941, 1}, {10715, 1}, {1953, 100} } },
    -- Enhancement Potion: Baby Crowdzilla
    {item=10771, ingredients={ {7663, 5}, {750, 5}, {9941, 1}, {10737, 1}, {10715, 1}, {1953, 100}, {1826, 100}, {2636, 25} } },
    -- Enhancement Potion: Wabbit
    {item=10772, ingredients={ {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10715, 1}, {10738, 1}, {9941, 1} } },
    -- Enhancement Potion: Walk
    {item=10773, ingredients={ {10715, 1}, {10739, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {9941, 1} } },
    -- Enhancement Potion: Willy Peninzias
    {item=10774, ingredients={ {7663, 5}, {750, 5}, {9941, 1}, {10740, 1}, {10715, 1}, {1826, 100}, {1953, 100}, {2636, 25} } },
    -- Enhancement Potion: Young Wild Boar
    {item=10775, ingredients={ {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {9941, 1}, {10715, 1}, {10741, 1} } },
    -- Enhancement Potion: Miniminotot
    {item=10776, ingredients={ {750, 5}, {10715, 1}, {9941, 1}, {10742, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5} } },
    -- Enhancement Potion: Tiwabbit Wosungwee
    {item=10777, ingredients={ {750, 5}, {10743, 1}, {9941, 1}, {10715, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5} } },
    -- Enhancement Potion: Borbat
    {item=10778, ingredients={ {10744, 1}, {10715, 1}, {9941, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5} } },
    -- Enhancement Potion: Black Dragoone
    {item=10779, ingredients={ {10715, 1}, {10745, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1} } },
    -- Enhancement Potion: Godfather's Gobtubby
    {item=10780, ingredients={ {2636, 25}, {750, 5}, {7663, 5}, {10746, 1}, {10715, 1}, {9941, 1}, {1826, 100}, {1953, 100} } },
    -- Enhancement Potion: Jellufo
    {item=10781, ingredients={ {750, 5}, {10747, 1}, {10715, 1}, {9941, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5} } },
    -- Enhancement Potion: Rushu's Shushu
    {item=10782, ingredients={ {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {9941, 1}, {10715, 1}, {10748, 1} } },
    -- Enhancement Potion: Vampyrina
    {item=10783, ingredients={ {9941, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {10715, 1}, {10749, 1} } },
    -- Enhancement Potion: Golden Dragoone
    {item=10809, ingredients={ {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10808, 1}, {10715, 1}, {9941, 1} } },
    -- Enhancement Potion: Ogrine Seeker
    {item=10811, ingredients={ {9941, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10810, 1}, {10715, 1} } },
    -- Enhancement Potion: Tarzantula
    {item=10883, ingredients={ {7663, 5}, {9941, 1}, {10879, 1}, {10715, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {750, 5} } },
    -- Enhancement Potion: Smush
    {item=10884, ingredients={ {10715, 1}, {10880, 1}, {9941, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {750, 5}, {7663, 5} } },
    -- Enhancement Potion: Tofrazzle
    {item=10885, ingredients={ {9941, 1}, {10715, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10881, 1} } },
    -- Enhancement Potion: Sting
    {item=10886, ingredients={ {10882, 1}, {9941, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {10715, 1} } },
    -- Enhancement Potion: Snow Bow Meow
    {item=11010, ingredients={ {750, 5}, {7663, 5}, {9941, 1}, {10715, 1}, {11009, 1}, {1826, 100}, {1953, 100}, {2636, 25} } },
    -- Brindled Minifoux Improvement Potion
    {item=11036, ingredients={ {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {10715, 1}, {11031, 1}, {9941, 1}, {1826, 100} } },
    -- Enhancement Potion: Komet
    {item=11467, ingredients={ {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {11470, 1}, {9941, 1}, {10715, 1}, {1826, 100} } },
    -- Shadowy Protection Potion
    {item=11700, ingredients={ {10217, 1}, {10219, 1}, {10215, 1}, {10211, 1}, {10208, 1}, {1462, 1}, {2378, 1}, {10218, 1} } },
    -- Opalescent Protection Potion
    {item=11701, ingredients={ {10213, 1}, {10212, 1}, {2364, 1}, {1462, 1}, {10209, 1}, {10216, 1}, {10214, 1}, {10210, 1} } },
    -- Enhancement Potion: Weegle Owl
    {item=11723, ingredients={ {750, 5}, {11718, 1}, {10715, 1}, {9941, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5} } },
    -- Enhancement Potion: Flibalak
    {item=11738, ingredients={ {2636, 25}, {750, 5}, {7663, 5}, {10715, 1}, {11716, 1}, {9941, 1}, {1826, 100}, {1953, 100} } },
    -- Enhancement Potion: Kwismas Reindeer
    {item=11741, ingredients={ {10715, 1}, {9941, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {750, 5}, {7663, 5}, {11720, 1} } },
    -- Enhancement Potion: Solar Bear
    {item=11742, ingredients={ {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10715, 1}, {11728, 1}, {9941, 1}, {1953, 100} } },
    -- Enhancement Potion: Pandiscamp
    {item=11765, ingredients={ {11762, 1}, {10715, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {9941, 1} } },
    -- Enhancement Potion: Tibilax
    {item=11769, ingredients={ {1953, 100}, {1826, 100}, {2636, 25}, {750, 5}, {7663, 5}, {10715, 1}, {9941, 1}, {11767, 1} } },
    -- Enhancement Potion: Minipoth
    {item=11778, ingredients={ {10715, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {11776, 1}, {9941, 1} } },
    -- Enhancement Potion: El Palador
    {item=11784, ingredients={ {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {9941, 1}, {11782, 1}, {10715, 1} } },
    -- Enhancement Potion: Borbat
    {item=11802, ingredients={ {11801, 1}, {1826, 100}, {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10715, 1}, {9941, 1} } },
    -- Enhancement Potion: Borbat
    {item=11807, ingredients={ {1826, 100}, {2636, 25}, {750, 5}, {7663, 5}, {11806, 1}, {10715, 1}, {9941, 1}, {1953, 100} } },
    -- Enhancement Potion: Tibilax
    {item=12751, ingredients={ {1826, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10715, 1}, {12748, 1}, {9941, 1}, {1953, 100} } },
    -- Enhancement Potion: Patrick the Sandcastle
    {item=12763, ingredients={ {1953, 100}, {2636, 25}, {7663, 5}, {750, 5}, {10715, 1}, {9941, 1}, {12762, 1}, {1826, 100} } },
    -- Enhancement Potion: Solar Bear
    {item=12822, ingredients={ {12820, 1}, {9941, 1}, {10715, 1}, {1953, 100}, {1826, 100}, {2636, 25}, {7663, 5}, {750, 5} } },
    -- Enhancement Potion: Karne
    {item=12825, ingredients={ {2636, 25}, {750, 5}, {7663, 5}, {12819, 1}, {9941, 1}, {10715, 1}, {1826, 100}, {1953, 100} } },
}

registerCraftSkill(23, sk23Crafts, {jobID = jobID, toolIDs=toolIDs}, ingredientsForCraftJob(jobID), jobID)
