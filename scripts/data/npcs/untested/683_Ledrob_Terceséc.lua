local npc = Npc(683, 9007)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1211)
    end
end

RegisterNPCDef(npc)
