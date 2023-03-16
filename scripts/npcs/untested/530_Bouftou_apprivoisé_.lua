local npc = Npc(530, 1566)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2291)
    end
end

RegisterNPCDef(npc)
