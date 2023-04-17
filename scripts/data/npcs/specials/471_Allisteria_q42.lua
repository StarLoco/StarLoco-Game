local npc = Npc(471, 11)
--TODO: Lié à la quête 42
npc.gender = 1
npc.colors = {4563915, 12353109, 14671666}
npc.accessories = {0, 0, 775, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1874)
    end
end

RegisterNPCDef(npc)
