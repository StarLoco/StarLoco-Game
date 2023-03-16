local npc = Npc(264, 40)

npc.colors = {14949638, 13762571, 9509652}
npc.accessories = {0, 0, 0, 1615, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1122)
    end
end

RegisterNPCDef(npc)
