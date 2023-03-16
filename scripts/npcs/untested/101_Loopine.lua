local npc = Npc(101, 9015)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(326, {292, 366})
    elseif answer == 292 then p:ask(356)
    elseif answer == 366 then p:ask(438)
    end
end

RegisterNPCDef(npc)