local npc = Npc(124, 9014)

npc.colors = {0, 0, 0}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(398, {317})
    elseif answer == 317 then p:ask(399)
    end
end

RegisterNPCDef(npc)