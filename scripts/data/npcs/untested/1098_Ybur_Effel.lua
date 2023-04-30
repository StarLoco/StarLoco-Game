local npc = Npc(1098, 9049)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6310)
    end
end

RegisterNPCDef(npc)
