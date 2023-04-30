local npc = Npc(813, 9007)

npc.colors = {13434111, 400205, 801446}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3379)
    end
end

RegisterNPCDef(npc)
