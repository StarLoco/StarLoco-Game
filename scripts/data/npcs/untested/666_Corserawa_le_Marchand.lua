local npc = Npc(666, 120)

npc.colors = {7372001, 13620450, 6518943}
npc.accessories = {0, 629, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(2737)
    end
end

RegisterNPCDef(npc)
