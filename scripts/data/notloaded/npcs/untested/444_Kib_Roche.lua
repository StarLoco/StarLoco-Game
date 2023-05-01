local npc = Npc(444, 9061)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1835, {1597})
    end
end

RegisterNPCDef(npc)
