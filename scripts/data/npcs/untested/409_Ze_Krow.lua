local npc = Npc(409, 9015)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1671)
    end
end

RegisterNPCDef(npc)
