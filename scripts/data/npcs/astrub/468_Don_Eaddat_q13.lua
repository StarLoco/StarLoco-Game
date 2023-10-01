local npc = Npc(468, 9050)
--TODO: Lié à la quête 13
---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1895, {1642})
    elseif answer == 1642 then p:ask(2289)
    end
end

RegisterNPCDef(npc)
