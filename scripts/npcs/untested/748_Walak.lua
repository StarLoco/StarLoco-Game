local npc = Npc(748, 1363)

npc.accessories = {0, 629, 0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(6754, {6151})
    elseif answer == 6151 then p:ask(6755, {6155, 6154, 6153, 6152})
    end
end

RegisterNPCDef(npc)
