local npc = Npc(712, 1243)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(7071, {6636})
    end
end

RegisterNPCDef(npc)
