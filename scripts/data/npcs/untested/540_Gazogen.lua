local npc = Npc(540, 9093)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2306)
    end
end

RegisterNPCDef(npc)
