local jobID = FishmongerJob
local toolIDs = {1946}

-- Craft for Prepare fish
local sk135Crafts = {
    -- Fried Breaded Fish **
    {item=1752, ingredients={ {1731, 1}, {1751, 1}, {1973, 1} } },
    -- Fried Breaded Fish ***
    {item=1753, ingredients={ {1731, 1}, {1973, 1}, {1751, 2} } },
    -- Fried Breaded Fish ****
    {item=1756, ingredients={ {1731, 1}, {1973, 1}, {1755, 1} } },
    -- Crab Stick **
    {item=1764, ingredients={ {1758, 1}, {1731, 1} } },
    -- Crab Stick ***
    {item=1765, ingredients={ {1758, 2}, {1731, 1} } },
    -- Crab Stick ****
    {item=1766, ingredients={ {1731, 1}, {1760, 1} } },
    -- Smoked Kittenfish **
    {item=1767, ingredients={ {1730, 1}, {1736, 1}, {1761, 1} } },
    -- Smoked Kittenfish ***
    {item=1768, ingredients={ {1761, 2}, {1730, 1}, {1736, 1} } },
    -- Smoked Tiger Fish ****
    {item=1769, ingredients={ {1736, 1}, {1763, 1} } },
    -- Grilled Bass **
    {item=1809, ingredients={ {1731, 3}, {1736, 2}, {1758, 2}, {1974, 1}, {1780, 1}, {1730, 10} } },
    -- Grilled Bass ***
    {item=1810, ingredients={ {1758, 2}, {1974, 2}, {1730, 10}, {1736, 4}, {1731, 4}, {1780, 2} } },
    -- Grawn Fritters **
    {item=1811, ingredients={ {1973, 1}, {1781, 1} } },
    -- Grawn Fritters ***
    {item=1812, ingredients={ {1781, 2}, {1973, 1} } },
    -- Sliced Gudgeon **
    {item=1813, ingredients={ {1783, 1}, {1736, 1} } },
    -- Sliced Gudgeon ***
    {item=1814, ingredients={ {1783, 2}, {1736, 1} } },
    -- Skate Wing **
    {item=1815, ingredients={ {1736, 4}, {1731, 3}, {1975, 2}, {1785, 1}, {1973, 6}, {380, 5} } },
    -- Skate Wing ***
    {item=1816, ingredients={ {1731, 4}, {1975, 3}, {1785, 2}, {1973, 8}, {380, 7}, {1736, 5} } },
    -- Steamed Carp ****
    {item=1817, ingredients={ {1730, 2}, {1736, 2}, {1731, 1}, {1797, 1} } },
    -- Steamed Carp **
    {item=1818, ingredients={ {1736, 2}, {1731, 1}, {1795, 1}, {1730, 2} } },
    -- Steamed Carp ****
    {item=1819, ingredients={ {1730, 2}, {1731, 2}, {1736, 2}, {1795, 2} } },
    -- Grilled Kralove **
    {item=1820, ingredients={ {1731, 4}, {1973, 2}, {1736, 2}, {1798, 1} } },
    -- Grilled Kralove ***
    {item=1821, ingredients={ {1736, 2}, {1798, 2}, {1731, 8}, {1973, 4} } },
    -- Seared Perch
    {item=1822, ingredients={ {395, 4}, {1736, 4}, {1730, 4}, {1731, 4}, {1973, 4}, {1802, 1} } },
    -- Seared Perch ***
    {item=1823, ingredients={ {1736, 6}, {395, 6}, {1802, 2}, {1973, 6}, {1731, 6}, {1730, 6} } },
    -- Seared Perch ****
    {item=1824, ingredients={ {1730, 4}, {1973, 4}, {1804, 1}, {1731, 4}, {395, 4}, {1736, 4} } },
    -- Fried Sardine **
    {item=1825, ingredients={ {1736, 2}, {1973, 2}, {1806, 1} } },
    -- Fried Sardine ***
    {item=1826, ingredients={ {1736, 3}, {1806, 2}, {1973, 2} } },
    -- Fried Sardine ****
    {item=1827, ingredients={ {1736, 2}, {1973, 2}, {1808, 1} } },
    -- Grilled Bass ****
    {item=1828, ingredients={ {1730, 10}, {1731, 3}, {1736, 2}, {1757, 2}, {1974, 1}, {1793, 1} } },
    -- Grawn Fritters ****
    {item=1829, ingredients={ {1973, 1}, {1787, 1} } },
    -- Sliced Gudgeon ****
    {item=1830, ingredients={ {1736, 1}, {1791, 1} } },
    -- Skate Wing ****
    {item=1831, ingredients={ {1973, 6}, {380, 5}, {1736, 4}, {1731, 3}, {1975, 2}, {1789, 1} } },
    -- Trout Flambé **
    {item=1832, ingredients={ {1736, 2}, {1845, 1} } },
    -- Trout Flambé ***
    {item=1833, ingredients={ {1736, 3}, {1845, 2} } },
    -- Trout Flambé ****
    {item=1834, ingredients={ {1736, 2}, {1976, 1} } },
    -- Stuffed Pike **
    {item=1835, ingredients={ {598, 2}, {1736, 2}, {1731, 2}, {1730, 2}, {1848, 1} } },
    -- Stuffed Pike ***
    {item=1836, ingredients={ {1731, 3}, {598, 3}, {1848, 2}, {1730, 3}, {1736, 3} } },
    -- Stuffed Pike ****
    {item=1837, ingredients={ {1851, 1}, {1730, 2}, {1736, 2}, {1731, 2}, {598, 2} } },
    -- Spicy Shark **
    {item=1838, ingredients={ {1977, 1}, {1730, 4}, {1975, 4}, {1731, 4}, {1973, 4}, {1736, 4}, {1852, 1} } },
    -- Spicy Shark ***
    {item=1839, ingredients={ {1731, 6}, {1736, 6}, {1852, 2}, {1977, 1}, {1973, 6}, {1975, 6}, {1730, 6} } },
    -- Spicy Shark ****
    {item=1840, ingredients={ {1854, 1}, {1975, 6}, {1731, 6}, {1730, 6}, {1974, 6}, {1736, 4} } },
    -- Grilled Kralove ****
    {item=1859, ingredients={ {1731, 4}, {1973, 2}, {1736, 2}, {1800, 1} } },
}

local requirements = {jobID = jobID, toolIDs = toolIDs}
registerCraftSkill(135, sk135Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
