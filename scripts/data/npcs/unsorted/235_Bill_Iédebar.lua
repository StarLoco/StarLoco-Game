local npc = Npc(235, 9004)
--TODO: Lié à la quête 62
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(944)
    end
end

RegisterNPCDef(npc)
