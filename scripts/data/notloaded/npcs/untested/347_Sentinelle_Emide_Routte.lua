local npc = Npc(347, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1395)
    end
end
--TODO QUETE ALIGNEMENT DEMON 27
RegisterNPCDef(npc)
