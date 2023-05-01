local npc = Npc(429, 11)

npc.gender = 1
npc.colors = {16777215, 1297118, 0}
npc.accessories = {0, 2096, 2534, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4565)
        -- QUAND CONDITION
    --elseif p:ask(1735, {1368, 1379, 1470})
    --elseif answer == 1379 then p:ask(1745)
    --elseif answer == 1368 then p:ask(1721)
    --elseif answer == 1470 then p:ask(1779)
    end
end
--TODO: check offi en étant féca
RegisterNPCDef(npc)
