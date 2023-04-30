local npc = Npc(490, 30)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2292)
    end
end

RegisterNPCDef(npc)
