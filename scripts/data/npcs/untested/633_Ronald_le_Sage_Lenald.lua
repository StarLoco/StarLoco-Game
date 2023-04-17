local npc = Npc(633, 1275)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2773)
    end
end

RegisterNPCDef(npc)
