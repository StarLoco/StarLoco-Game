local npc = Npc(864, 9039)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3698, {3241, 20040})
    elseif answer == 3241 then p:ask(3700, {324})
    end
end

RegisterNPCDef(npc)
