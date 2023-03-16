local npc = Npc(125, 9045)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(400, {318})
    elseif answer == 318 then p:ask(401)
    end
end

RegisterNPCDef(npc)