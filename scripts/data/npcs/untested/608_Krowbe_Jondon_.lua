local npc = Npc(608, 9016)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2463, {2121})
    end
end

RegisterNPCDef(npc)
