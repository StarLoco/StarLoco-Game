local npc = Npc(1165, 1265)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7072, {6638})
    end
end

RegisterNPCDef(npc)
