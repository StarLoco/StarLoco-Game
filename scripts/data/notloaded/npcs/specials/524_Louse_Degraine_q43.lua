local npc = Npc(524, 101)
--TODO: Lié à la quête 43
npc.gender = 1
npc.colors = {11121888, 10490393, 15056833}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2245, {1878})
    end
end

RegisterNPCDef(npc)
