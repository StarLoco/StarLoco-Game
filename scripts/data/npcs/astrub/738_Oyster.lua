local npc = Npc(738, 9000)

npc.colors = {16777215, 15129637, 16777215}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3043)
    end
end

RegisterNPCDef(npc)
