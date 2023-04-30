local npc = Npc(494, 80)

npc.colors = {5403090, 5980970, 13086174}
npc.accessories = {0, 2096, 777, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2028, {171})
    end
end

RegisterNPCDef(npc)
