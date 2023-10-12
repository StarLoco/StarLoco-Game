local npc = Npc(387, 1155)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1622)
    end
end

RegisterNPCDef(npc)
