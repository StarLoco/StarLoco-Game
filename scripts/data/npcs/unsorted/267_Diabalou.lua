local npc = Npc(267, 100)

npc.colors = {3020823, 14491937, 10424858}
npc.accessories = {0, 707, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1107)
    end
end

RegisterNPCDef(npc)
