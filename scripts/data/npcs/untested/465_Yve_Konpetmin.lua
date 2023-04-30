local npc = Npc(465, 9018)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1944)
    end
end

RegisterNPCDef(npc)
