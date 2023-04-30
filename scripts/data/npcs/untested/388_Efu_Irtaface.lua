local npc = Npc(388, 1155)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1697)
    end
end

RegisterNPCDef(npc)
