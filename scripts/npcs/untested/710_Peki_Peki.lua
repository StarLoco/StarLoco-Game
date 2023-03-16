local npc = Npc(710, 1297)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2944, {2580})
    end
end

RegisterNPCDef(npc)