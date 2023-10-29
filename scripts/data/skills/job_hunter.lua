local jobID = HunterJob

-- Craft for Prepare
local sk132Crafts = {
    -- Preserved Bird Meat
    {item=1987, ingredients={ {1986, 2}, {1896, 1} } },
    -- Preserved Bird Meat **
    {item=1988, ingredients={ {1986, 2}, {1897, 1} } },
    -- Preserved Bird Meat ****
    {item=1989, ingredients={ {1986, 2}, {1933, 1} } },
    -- Preserved Larva Flesh
    {item=1990, ingredients={ {1986, 1}, {1898, 1} } },
    -- Preserved Larva Flesh **
    {item=1991, ingredients={ {1899, 1}, {1986, 1} } },
    -- Preserved Larva Flesh ***
    {item=1992, ingredients={ {1986, 1}, {1900, 1} } },
    -- Preserved Insect Flesh
    {item=1993, ingredients={ {1986, 1}, {1983, 1}, {1915, 1} } },
    -- Preserved Insect Flesh **
    {item=1994, ingredients={ {1986, 4}, {1983, 2}, {1916, 1} } },
    -- Preserved Gobball Leg
    {item=1995, ingredients={ {1911, 1}, {1986, 1}, {1983, 1} } },
    -- Preserved Gobball Leg **
    {item=1997, ingredients={ {1986, 2}, {1983, 2}, {1912, 1} } },
    -- Preserved Gobball Leg ***
    {item=1998, ingredients={ {1983, 3}, {1986, 3}, {1913, 1} } },
    -- Preserved Gobball Leg ****
    {item=1999, ingredients={ {1914, 1}, {1983, 4}, {1986, 4} } },
    -- Preserved Wabbit Leg
    {item=2000, ingredients={ {1986, 1}, {1983, 1}, {1902, 1}, {1985, 1} } },
    -- Preserved Wabbit Leg **
    {item=2001, ingredients={ {1986, 2}, {1983, 2}, {1985, 1}, {1903, 1} } },
    -- Preserved Wabbit Leg ***
    {item=2002, ingredients={ {1986, 3}, {1983, 2}, {1985, 2}, {1905, 1} } },
    -- Preserved Wabbit Leg ****
    {item=2003, ingredients={ {1985, 3}, {1983, 3}, {1986, 3}, {1901, 1} } },
    -- Preserved Pork Loin
    {item=2004, ingredients={ {1986, 2}, {1983, 2}, {1984, 1}, {1917, 1} } },
    -- Preserved Pork Loin **
    {item=2005, ingredients={ {1918, 1}, {1986, 3}, {1983, 2}, {1984, 2} } },
    -- Preserved Pork Loin ***
    {item=2006, ingredients={ {1984, 3}, {1921, 1}, {1986, 4}, {1983, 3} } },
    -- Preserved Pork Loin ****
    {item=2007, ingredients={ {1984, 4}, {1919, 1}, {1983, 5}, {1986, 5} } },
    -- Preserved Muzzle
    {item=2008, ingredients={ {1927, 1}, {1986, 1}, {2012, 1}, {1985, 1} } },
    -- Preserved Muzzle **
    {item=2009, ingredients={ {1986, 3}, {2012, 2}, {1985, 1}, {1929, 1} } },
    -- Preserved Muzzle ***
    {item=2010, ingredients={ {1986, 4}, {2012, 3}, {1985, 2}, {1930, 1} } },
    -- Preserved Dragomeat
    {item=2014, ingredients={ {1986, 4}, {2012, 2}, {1984, 2}, {1985, 1}, {1922, 1} } },
    -- Preserved Dragomeat **
    {item=2015, ingredients={ {1986, 4}, {2012, 2}, {1984, 2}, {1923, 1}, {1985, 1} } },
    -- Preserved Dragomeat ***
    {item=2016, ingredients={ {2012, 3}, {1984, 3}, {1985, 2}, {1924, 1}, {1986, 4} } },
    -- Preserved Dragomeat ****
    {item=2017, ingredients={ {1985, 3}, {1926, 1}, {1986, 4}, {1984, 4}, {2012, 4} } },
    -- Preserved Kaniger Meat
    {item=8501, ingredients={ {1984, 1}, {1983, 1}, {8498, 1}, {311, 1}, {1985, 1}, {1986, 1} } },
    -- Preserved Koalak Meat
    {item=8502, ingredients={ {2012, 1}, {311, 1}, {396, 1}, {1985, 1}, {8499, 1}, {1984, 1}, {1983, 1}, {1986, 1} } },
    -- Preserved Crocodyl Meat
    {item=8503, ingredients={ {311, 1}, {2012, 1}, {8500, 1}, {1986, 1}, {1984, 1}, {1983, 1}, {1985, 1} } },
    -- Preserved Demon Meat
    {item=9771, ingredients={ {2012, 2}, {311, 1}, {397, 1}, {9764, 1}, {1983, 1}, {1985, 1}, {1986, 4}, {1984, 2} } },
    -- Preserved Demon Meat**
    {item=9773, ingredients={ {1983, 1}, {1986, 4}, {2012, 2}, {311, 2}, {1984, 2}, {397, 1}, {9766, 1}, {1985, 1} } },
    -- Preserved Demon Meat***
    {item=9774, ingredients={ {397, 1}, {1986, 4}, {1984, 3}, {2012, 3}, {311, 3}, {1985, 2}, {1983, 2}, {9767, 1} } },
    -- Preserved Demon Meat****
    {item=9775, ingredients={ {1985, 3}, {9768, 1}, {397, 1}, {1986, 4}, {311, 4}, {2012, 4}, {1984, 3}, {1983, 3} } },
    -- Preserved Angel Meat***
    {item=9776, ingredients={ {1986, 5}, {311, 3}, {2012, 3}, {1984, 3}, {1985, 2}, {9769, 1}, {389, 1}, {1983, 8} } },
    -- Preserved Angel Meat****
    {item=9777, ingredients={ {389, 1}, {9770, 1}, {1983, 9}, {1986, 6}, {2012, 4}, {311, 4}, {1985, 3}, {1984, 3} } },
    -- Preserved Angel Meat
    {item=11571, ingredients={ {311, 1}, {1986, 4}, {1984, 2}, {2012, 2}, {389, 1}, {1985, 1}, {1983, 1}, {11569, 1} } },
    -- Preserved Angel Meat**
    {item=11572, ingredients={ {1983, 1}, {389, 1}, {1986, 4}, {1984, 2}, {311, 2}, {2012, 2}, {11570, 1}, {1985, 1} } },
}

local requirements = {jobID = jobID} -- FIXME
registerCraftSkill(132, sk132Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
