local npc = Npc(627, 9075)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2522)
    end
end

RegisterNPCDef(npc)
