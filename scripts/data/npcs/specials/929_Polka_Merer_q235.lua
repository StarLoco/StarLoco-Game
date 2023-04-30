local npc = Npc(929, 21)
--TODO: Lié à la quête 235
npc.gender = 1
npc.colors = {15988728, 6331037, 16514301}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4143)
    end
end

RegisterNPCDef(npc)
