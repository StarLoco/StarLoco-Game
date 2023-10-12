local npc = Npc(802, 31)
--TODO: Lié à la quête 142
npc.gender = 1
npc.colors = {10754718, 14537767, 12828596}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3306)
    end
end

RegisterNPCDef(npc)
