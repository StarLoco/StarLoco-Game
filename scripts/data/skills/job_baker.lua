local jobID = BakerJob
local toolIDs = {492}

-- Craft for Bake bread
local sk27Crafts = {
    -- Amaknan Bread
    {item=468, ingredients={ {311, 1}, {285, 1} } },
    -- Poppy Seed Bread
    {item=520, ingredients={ {300, 1}, {311, 1}, {286, 1}, {285, 1} } },
    -- Sesame Seed Bread
    {item=521, ingredients={ {582, 1}, {287, 1}, {311, 1}, {286, 1} } },
    -- Flax Seed Bread
    {item=522, ingredients={ {285, 1}, {286, 1}, {422, 1}, {311, 1} } },
    -- Hazelnut Bread
    {item=524, ingredients={ {394, 1}, {285, 1}, {311, 1}, {286, 1} } },
    -- Cereal Bread
    {item=526, ingredients={ {286, 1}, {311, 1}, {531, 1}, {285, 1}, {530, 1}, {529, 1} } },
    -- Walnut Bread
    {item=527, ingredients={ {391, 1}, {392, 1}, {286, 1}, {311, 1}, {285, 1} } },
    -- Wholemeal Bread
    {item=528, ingredients={ {289, 1}, {286, 1}, {285, 1}, {311, 1} } },
    -- Oatflake Bread
    {item=536, ingredients={ {286, 1}, {311, 1}, {531, 1} } },
    -- Rye Bread
    {item=539, ingredients={ {311, 1}, {530, 1}, {286, 1}, {532, 1} } },
    -- Solid Bread
    {item=692, ingredients={ {690, 1}, {311, 1} } },
    -- Field Bread
    {item=1737, ingredients={ {285, 1}, {690, 1}, {286, 1}, {311, 1}, {534, 1} } },
    -- City Bread
    {item=1738, ingredients={ {534, 1}, {311, 1}, {405, 1} } },
    -- Golden Bread
    {item=2020, ingredients={ {286, 1}, {2019, 1}, {311, 1} } },
    -- Briochette
    {item=2024, ingredients={ {311, 1}, {535, 1}, {286, 1}, {367, 1} } },
    -- Magic Briochette
    {item=2025, ingredients={ {367, 1}, {2022, 1}, {286, 1}, {311, 1} } },
    -- Magic Solid Bread
    {item=2028, ingredients={ {311, 1}, {2027, 1} } },
    -- Resistant Rye Bread
    {item=2031, ingredients={ {2030, 1}, {286, 1}, {311, 1} } },
    -- Gold-Bearing Rolled Oat Bread
    {item=2038, ingredients={ {286, 1}, {311, 1}, {2037, 1} } },
    -- Qui Leure Bread
    {item=2635, ingredients={ {582, 1}, {531, 1}, {529, 1}, {586, 1}, {286, 1}, {311, 1}, {583, 1} } },
    -- Tahde Bread
    {item=2636, ingredients={ {587, 1}, {1896, 1}, {583, 1}, {531, 1}, {285, 1}, {582, 1}, {286, 1}, {311, 1} } },
    -- Intre Bread
    {item=10079, ingredients={ {534, 1}, {535, 1}, {286, 1}, {8500, 1}, {6672, 1}, {399, 1}, {7046, 1} } },
}

-- Craft for Make candies
local sk109Crafts = {
    -- Greenjely
    {item=993, ingredients={ {369, 2} } },
    -- Redjely
    {item=994, ingredients={ {368, 2} } },
    -- Blujely
    {item=995, ingredients={ {757, 2} } },
    -- Barley Sugar
    {item=2034, ingredients={ {311, 1}, {2033, 1} } },
    -- TODO: secret recipes
}


local requirements = {jobID = jobID, toolIDs = toolIDs}
registerCraftSkill(27, sk27Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
registerCraftSkill(109, sk109Crafts, requirements, ingredientsForCraftJob(jobID), jobID)
