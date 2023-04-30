local npc = Npc(162, 9023)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(608, {512})
    elseif answer == 512 then p:ask(610, {514})
    end
end

RegisterNPCDef(npc)
