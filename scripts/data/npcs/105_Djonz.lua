local npc = Npc(105, 9036)

npc.gender = 1

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(355)
    end
end

RegisterNPCDef(npc)
