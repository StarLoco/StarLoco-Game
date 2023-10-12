local npc = Npc(978, 1205)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(4665)
    end
end

RegisterNPCDef(npc)
