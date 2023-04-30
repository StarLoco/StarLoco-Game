---@param id number NpcTemplateID
local function createBankClerk(id)
    local npc = Npc(id, 9048)

    npc.colors = {-1, -1, 15184663}

    ---@param p Player
    function npc:onTalk(p, answer)
        if answer == 0 then p:ask(318, {259,329}, "[bankCost]")
        elseif answer == 259 then p:openBank()
        elseif answer == 329 then p:ask(410) end
    end

    RegisterNPCDef(npc)
end

createBankClerk(100)
-- Astrub
createBankClerk(520)
createBankClerk(522)
-- Pandala
createBankClerk(691)
createBankClerk(692)
