local npc = Npc(106, 9047)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(357, {294, 293})
    elseif answer == 293 then p:ask(359)
    elseif answer == 294 then p:ask(358)
    end
end

RegisterNPCDef(npc)