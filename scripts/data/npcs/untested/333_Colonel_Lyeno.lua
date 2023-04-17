local npc = Npc(333, 1001)

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1317)
    end
end

RegisterNPCDef(npc)
