local npc = Npc(497, 20)

npc.accessories = {0, 0, 772, 2074, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2221)
    end
end

RegisterNPCDef(npc)
