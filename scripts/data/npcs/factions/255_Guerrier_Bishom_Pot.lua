local npc = Npc(255, 1205)
--TODO: Lié à la quête Alignement 13 Bonta
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1080)
    end
end

RegisterNPCDef(npc)
