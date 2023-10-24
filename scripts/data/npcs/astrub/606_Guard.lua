local npc = Npc(606, 1211)

local wantedResponseQuestObjectives = {
    {response=2113, item=6875, quest=34, objective=207}, -- Akornadikt
    {response=2109, item=6871, quest=30, objective=213}, -- Frakacia

    {response=2552, item=7353, quest=117, objective=425}, -- Marzwel
}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    -- See Wanted.lua
end

RegisterNPCDef(npc)
