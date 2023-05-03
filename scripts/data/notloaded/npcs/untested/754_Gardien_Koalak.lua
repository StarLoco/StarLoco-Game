local npc = Npc(754, 1363)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3119, {2742})
    end
end

RegisterNPCDef(npc)
