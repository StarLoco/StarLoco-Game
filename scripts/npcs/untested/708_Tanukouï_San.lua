local npc = Npc(708, 1283)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2942, {2578})
    end
end

RegisterNPCDef(npc)