local npc = Npc(883, 9008)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3745)
    end
end

RegisterNPCDef(npc)
