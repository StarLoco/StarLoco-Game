local npc = Npc(198, 7000)
--TODO: Lié à la quête Alignement Bonta 35
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1558)
    end
end

RegisterNPCDef(npc)
