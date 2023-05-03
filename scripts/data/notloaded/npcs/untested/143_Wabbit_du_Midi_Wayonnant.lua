local npc = Npc(143, 9054)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(468, {392})
    end
end

RegisterNPCDef(npc)
