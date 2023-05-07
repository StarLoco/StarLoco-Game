local npc = Npc(477, 31)
--TODO: Lié à la quête 53
npc.gender = 1
npc.colors = {3418033, 16777215, 16777215}
npc.accessories = {0, 0, 0, 6604, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(1852, {1610})
    elseif answer == 1610 then p:ask(1854)
    end
end

RegisterNPCDef(npc)
